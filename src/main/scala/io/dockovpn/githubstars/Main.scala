package io.dockovpn.githubstars

import cats.effect.{IO, IOApp, Temporal}
import cron4s.Cron
import eu.timepit.fs2cron.cron4s.Cron4sScheduler
import fs2.Stream
import fs2.concurrent.SignallingRef
import org.http4s.client.JavaNetClientBuilder

import scala.concurrent.duration.DurationInt

object Main extends IOApp.Simple {
  
  private val httpClient = JavaNetClientBuilder[IO].create
  private val getStars = Stream.eval(
    httpClient.expect[String]("https://ip.dockovpn.io")
      .flatMap(IO.println)
  )
  
  override def run: IO[Unit] = {
    val cronScheduler = Cron4sScheduler.systemDefault[IO]
    val evenSeconds = Cron.unsafeParse("*/2 * * ? * *")
    val scheduled = cronScheduler.awakeEvery(evenSeconds) >> getStars
    val cancel = SignallingRef[IO, Boolean](false)
    
    for {
      c <- cancel
      s <- scheduled.interruptWhen(c).repeat.compile.drain.start
      //prints about 5 times before stop
      _ <- Temporal[IO].sleep(10.seconds) >> c.set(true)
    } yield s
  }
}
