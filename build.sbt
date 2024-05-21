name := """play-andeno"""
organization := "com.andeno"

licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0"))

version := "3.0.0"

scalaVersion := "2.13.14"

crossScalaVersions := Seq("2.13.14")

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play" % "2.9.3",
  "com.typesafe.play" %% "play-slick" % "5.2.0",
  "com.typesafe.slick" %% "slick-codegen" % "3.4.1",
  "com.github.tminglei" %% "slick-pg" % "0.21.1",
  "com.github.tminglei" %% "slick-pg_play-json" % "0.21.1"
)

val scalastyleDir = Def.setting(baseDirectory.value / "project")

scalastyleConfig := scalastyleDir.value / "scalastyle-config.xml"
scalastyleFailOnError := true

githubOwner := "jchernan"
githubRepository := "play-andeno"
