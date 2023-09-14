ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

val http4sVersion = "0.23.23"

lazy val root = (project in file("."))
  .settings(
    name := "github-stars-counter",
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-unchecked",
      "-language:postfixOps"
    ),
    libraryDependencies ++= Seq(
      "io.dockovpn" %% "metastore" % "0.3.1-SNAPSHOT",
      "eu.timepit" %% "fs2-cron-cron4s" % "0.8.3" withSources() withJavadoc(),
      "org.typelevel" %% "cats-effect" % "3.5.1" withSources() withJavadoc(),
      "co.fs2" %% "fs2-core" % "3.9.1" withSources() withJavadoc(),
      "org.http4s" %% "http4s-ember-client" % http4sVersion,
      "org.http4s" %% "http4s-dsl"          % http4sVersion,
      "org.http4s" %% "http4s-circe" % http4sVersion,
      // Optional for auto-derivation of JSON codecs
      "io.circe" %% "circe-generic" % "0.14.6",
      // Optional for string interpolation to JSON model
      "io.circe" %% "circe-literal" % "0.14.6"
    )
  )
