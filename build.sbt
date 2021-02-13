name := """play-andeno"""
organization := "com.andeno"

licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0"))

version := "1.1.0"

scalaVersion := "2.12.13"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play" % "2.7.9",
  "com.typesafe.play" %% "play-slick" % "4.0.2",
  "com.typesafe.slick" %% "slick-codegen" % "3.3.3",
  "com.github.tminglei" %% "slick-pg" % "0.19.4",
  "com.github.tminglei" %% "slick-pg_play-json" % "0.19.4",
  "org.scalatest" %% "scalatest" % "3.0.9" % "test"
)

val scalastyleDir = Def.setting(baseDirectory.value / "project")

scalastyleConfig := scalastyleDir.value / "scalastyle-config.xml"
scalastyleFailOnError := true

githubOwner := "jchernan"
githubRepository := "play-andeno"

