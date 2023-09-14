
ThisBuild / mainClass := Some("io.dockovpn.githubstars.Main")
ThisBuild / organization := "io.dockovpn"
ThisBuild / version := "0.1.0"
ThisBuild / scalaVersion := "2.13.12"

ThisBuild / assemblyMergeStrategy := {
  case PathList(ps @ _*) if ps.last == "module-info.class" =>
    MergeStrategy.discard
  case x =>
    val oldStrategy = (ThisBuild / assemblyMergeStrategy).value
    oldStrategy(x)
}

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
      "com.github.pureconfig" %% "pureconfig" % "0.17.2",
      "eu.timepit" %% "fs2-cron-cron4s" % "0.8.3" withSources() withJavadoc(),
      "org.typelevel" %% "cats-effect" % "3.5.1" withSources() withJavadoc(),
      "co.fs2" %% "fs2-core" % "3.9.1" withSources() withJavadoc(),
      "org.http4s" %% "http4s-ember-client" % http4sVersion,
      "org.http4s" %% "http4s-dsl"          % http4sVersion,
      "org.http4s" %% "http4s-circe" % http4sVersion,
      "io.circe" %% "circe-generic" % "0.14.6",
      "io.circe" %% "circe-literal" % "0.14.6"
    )
  )

addCommandAlias(
  "build",
  """|;
     |clean;
     |assembly;
  """.stripMargin)
