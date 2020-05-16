name := """play-andeno"""
organization := "com.andeno"

homepage := Some(url("https://github.com/jchernan/play-andeno"))

licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0"))

version := "0.1.0"

scalaVersion := "2.12.11"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play" % "2.6.25",
  "com.typesafe.play" %% "play-slick" % "3.0.4",
  "com.typesafe.slick" %% "slick-codegen" % "3.3.2",
  "com.github.tminglei" %% "slick-pg" % "0.19.0",
  "com.github.tminglei" %% "slick-pg_play-json" % "0.19.0",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)

val scalastyleDir = Def.setting(baseDirectory.value / "project")

scalastyleConfig := scalastyleDir.value / "scalastyle-config.xml"
scalastyleFailOnError := true

publishMavenStyle := true
pomIncludeRepository := { _ => false }

bintrayPackageLabels := Seq("play", "playframework")
bintrayVcsUrl := Some("""git@github.com:jchernan/play-andeno.git""")
