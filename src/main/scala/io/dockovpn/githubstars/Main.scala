package io.dockovpn.githubstars

import cats.effect.{ExitCode, IO, IOApp}
import cron4s.Cron
import eu.timepit.fs2cron.cron4s.Cron4sScheduler
import fs2.Stream
import io.circe.Json
import io.dockovpn.githubstars.config.AppConfig
import io.dockovpn.githubstars.db.TableMetadataProvider
import io.dockovpn.githubstars.domain.StarsCount
import io.dockovpn.githubstars.service.GithubStarsService
import io.dockovpn.metastore.db.DBRef
import io.dockovpn.metastore.provider.StoreProvider
import io.dockovpn.metastore.store.AbstractStore
import io.dockovpn.metastore.util.Lazy.lazily
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.ExecutionContext.Implicits._

object Main extends IOApp {
  private val appConfig = AppConfig()
  private val starsCounterConfig = appConfig.githubStarsCounterConfig
  
  private implicit val metadataProvider: TableMetadataProvider = new TableMetadataProvider()
  private implicit val dbRef: DBRef = lazily { Database.forConfig("", appConfig.slickConfig) }
  
  private val store: AbstractStore[StarsCount] = StoreProvider.getStoreByType(starsCounterConfig.storeType)
  private val service = new GithubStarsService(store)
  private val httpClientResource = EmberClientBuilder.default[IO].build
  
  private def getStars(httpClient: Client[IO]): Stream[IO, Int] =
    Stream.emits(starsCounterConfig.repos.split(",").map(_.trim))
      .flatMap { repo =>
        Stream.eval(
          for {
            json <- httpClient.expect[Json](s"https://api.github.com/repos/$repo")
            starsCount = json.asObject.get("watchers_count").get.as[Int].toOption.get
            _ <- IO.println(s"Repo: $repo; stars: $starsCount")
            _ <- service.addRecord(repo, starsCount)
          } yield starsCount
        )
      }
  
  override def run(args: List[String]): IO[ExitCode] = {
    val cronScheduler = Cron4sScheduler.systemDefault[IO]
    val timeInterval = Cron.unsafeParse(starsCounterConfig.cron)
    
    for {
      httpClientAllocated <- httpClientResource.allocated
      (httpClient, releaseHttpClient) = httpClientAllocated
      effect = getStars(httpClient).handleErrorWith { t =>
        Stream.eval(IO.println(t.getMessage))
      }
      scheduled = cronScheduler.awakeEvery(timeInterval) >> effect
      _ <- scheduled.repeat.compile.drain.start
      _ <- IO.never.void
      _ <- releaseHttpClient >> IO.println("Releasing HTTP client")
    } yield ExitCode.Success
  }
}
