name := "lojinha"

version := "1.0-SNAPSHOT"

lazy val root =
  (project in file("."))
    .enablePlugins(PlayScala)

scalaVersion := "2.12.12"

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
  "org.webjars" %% "webjars-play" % "2.6.3",
  "org.webjars" %  "jquery"       % "2.2.4",
  "org.webjars" %  "bootstrap"    % "3.4.1"
)

val databaseDeps = Seq(
  jdbc,
  evolutions,
  "com.h2database" % "h2" % "1.4.200"
)

val testLibs = Seq(
  "org.mockito" % "mockito-core" % "3.5.11",
  specs2
).map(_ % Test)

libraryDependencies ++= Seq(
  guice,
  // fixme: replace ehcache with caffeine in play 2.7 to make this more testable
  ehcache,
  "com.amazonaws"     %  "aws-java-sdk" % "1.11.882",
  "com.typesafe.play" %% "anorm"        % "2.5.3",
  "com.typesafe.play" %% "play-mailer"  % "6.0.1",
  "postgresql"        %  "postgresql"   % "9.1-901.jdbc4" % Runtime
) ++ webJars ++ databaseDeps ++ testLibs

TwirlKeys.templateImports ++= Seq(
  "models.dao._",
  "models.images._"
)
