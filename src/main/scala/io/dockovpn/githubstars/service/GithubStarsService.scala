package io.dockovpn.githubstars.service

import cats.effect.IO
import io.dockovpn.githubstars.domain.StarsCount
import io.dockovpn.metastore.store.AbstractStore

import java.sql.Timestamp
import java.time.Instant
import java.util.UUID

class GithubStarsService(store: AbstractStore[StarsCount]) {
  
  def addRecord(repoName: String, starsCount: Int): IO[Unit] = for {
    record <- IO(StarsCount(
      recordId = UUID.randomUUID().toString,
      repoName = repoName,
      stars = starsCount,
      timeCreated = Timestamp.from(Instant.now()),
    ))
    
    _ <- IO.fromFuture(IO(store.put(record.recordId, record)))
  } yield ()
}
