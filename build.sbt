ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

lazy val root = (project in file("."))
  .settings(
    name := "github-stars-counter",
    libraryDependencies ++= Seq(
      "eu.timepit" %% "fs2-cron-cron4s" % "0.8.3" withSources() withJavadoc(),
      "org.typelevel" %% "cats-effect" % "3.5.1" withSources() withJavadoc(),
      "co.fs2" %% "fs2-core" % "3.9.1" withSources() withJavadoc()
    )
  )
