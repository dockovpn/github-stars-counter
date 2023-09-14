package io.dockovpn.githubstars.domain

import java.sql.Timestamp

case class StarsCount(
  recordId: String,
  repoName: String,
  stars: Int,
  timeCreated: Timestamp,
)
