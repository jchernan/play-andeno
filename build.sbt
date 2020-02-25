name := """play-andeno"""
organization := "com.andeno"

sbtPlugin := true

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play" % "2.6.25",
  "com.typesafe.play" %% "play-slick" % "3.0.4",
  "com.typesafe.slick" %% "slick-codegen" % "3.3.2",
  "com.github.tminglei" %% "slick-pg" % "0.18.1",
  "com.github.tminglei" %% "slick-pg_play-json" % "0.18.1",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)

val scalastyleDir = Def.setting(baseDirectory.value / "project")

scalastyleConfig := scalastyleDir.value / "scalastyle-config.xml"
scalastyleFailOnError := true
