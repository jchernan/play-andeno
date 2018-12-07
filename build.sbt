name := """play-andeno"""
organization := "com.andeno"

sbtPlugin := true

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play" % "2.6.11",
  "com.typesafe.play" %% "play-slick" % "3.0.3",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)

val scalastyleDir = Def.setting(baseDirectory.value / "project")

scalastyleConfig := scalastyleDir.value / "scalastyle-config.xml"
scalastyleFailOnError := true
