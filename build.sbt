name := "lojinha"

version := "1.0-SNAPSHOT"

lazy val root =
  (project in file("."))
    .enablePlugins(PlayScala)

scalaVersion := "2.13.3"

scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked",
  "-language:postfixOps",
  "-language:implicitConversions",
// fixme: cannot be used for now because lots of erros pop up in routes and templates
//  "-Ywarn-unused",
//  "-Ywarn-unused-import",
//  "-Xlint",
  "-Xfatal-warnings"
)

Global / onChangedBuildSource := ReloadOnSourceChanges

val webJars = Seq(
  "org.webjars" %% "webjars-play" % "2.8.0",
  "org.webjars" %  "jquery"       % "2.2.4",
  "org.webjars" %  "bootstrap"    % "3.4.1"
)

val databaseDeps = Seq(
  jdbc,
  evolutions,
  "org.playframework.anorm" %% "anorm" % "2.6.7",
  "com.h2database"           % "h2"    % "1.4.200"
)

val testLibs = Seq(
  "org.mockito" % "mockito-core" % "3.5.15",
  specs2
).map(_ % Test)

libraryDependencies ++= Seq(
  guice,
  caffeine,
  "com.amazonaws"     %  "aws-java-sdk" % "1.11.885",
  "com.typesafe.play" %% "play-mailer"  % "8.0.1",
  "postgresql"        %  "postgresql"   % "9.1-901.jdbc4" % Runtime
) ++ webJars ++ databaseDeps ++ testLibs

TwirlKeys.templateImports ++= Seq(
  "models.dao._",
  "models.images._"
)
