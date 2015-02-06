name := "lojinha"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.5"

libraryDependencies ++= Seq(
  cache,
  jdbc,
  anorm,
  "com.amazonaws" % "aws-java-sdk" % "1.9.17",
  "com.typesafe.play" %% "play-mailer" % "2.4.0",
  "postgresql" % "postgresql" % "9.1-901.jdbc4" % "runtime",
  "org.mockito" % "mockito-core" % "1.10.19" % "test"
)

TwirlKeys.templateImports ++= Seq(
  "models.dao._"
)
