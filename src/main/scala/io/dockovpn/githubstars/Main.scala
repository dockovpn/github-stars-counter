package io.dockovpn.githubstars

import cats.effect.{ExitCode, IO, IOApp, Temporal}
import cron4s.Cron
import eu.timepit.fs2cron.cron4s.Cron4sScheduler
import fs2.Stream
import io.circe.Json
import io.dockovpn.githubstars.db.TableMetadataProvider
import io.dockovpn.githubstars.domain.StarsCount
import io.dockovpn.githubstars.service.GithubStarsService
import io.dockovpn.metastore.provider.StoreProvider
import io.dockovpn.metastore.store.{AbstractStore, StoreType}
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.client.JavaNetClientBuilder

import scala.concurrent.ExecutionContext.Implicits._

object Main extends IOApp {
  private implicit val metadataProvider: TableMetadataProvider = new TableMetadataProvider()
  private val store: AbstractStore[StarsCount] = StoreProvider.getStoreByType(StoreType.MapStoreType)
  private val service = new GithubStarsService(store)
  private val httpClient = JavaNetClientBuilder[IO].create
  private val getStars = Stream.eval(
    for {
      json <- httpClient.expect[Json]("https://api.github.com/repos/dockovpn/dockovpn")
      starsCount <- IO(json.asObject.get.apply("watchers_count").get.as[Int].toOption.get)
      _ <- IO.println(starsCount)
      _ <- service.addRecord(starsCount)
    } yield starsCount
  )
  
  override def run(args: List[String]): IO[ExitCode] = {
    val cronScheduler = Cron4sScheduler.systemDefault[IO]
    val evenSeconds = Cron.unsafeParse("0 * * ? * *")
    val scheduled = cronScheduler.awakeEvery(evenSeconds) >> getStars
    
    for {
      _ <- scheduled.repeat.compile.drain.start
      _ <- Temporal[IO].never >> IO.pure(())
    } yield ExitCode.Success
  }
}
