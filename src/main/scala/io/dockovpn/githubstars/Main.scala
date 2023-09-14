package io.dockovpn.githubstars

import cats.effect.{ExitCode, IO, IOApp, Temporal}
import cron4s.Cron
import eu.timepit.fs2cron.cron4s.Cron4sScheduler
import fs2.Stream
import io.circe.Json
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.client.JavaNetClientBuilder

object Main extends IOApp {
  
  private val httpClient = JavaNetClientBuilder[IO].create
  private val getStars = Stream.eval(
    for {
      json <- httpClient.expect[Json]("https://api.github.com/repos/dockovpn/dockovpn")
      starsCount <- IO(json.asObject.get.apply("watchers_count").get)
      _ <- IO.println(starsCount)
    } yield starsCount
  )
  
  override def run(args: List[String]): IO[ExitCode] = {
    val cronScheduler = Cron4sScheduler.systemDefault[IO]
    val evenSeconds = Cron.unsafeParse("*/2 * * ? * *")
    val scheduled = cronScheduler.awakeEvery(evenSeconds) >> getStars
    
    for {
      _ <- scheduled.repeat.compile.drain.start
      _ <- Temporal[IO].never >> IO.pure(())
    } yield ExitCode.Success
  }
}
