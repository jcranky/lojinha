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

scalafmtOnCompile := true

Global / onChangedBuildSource := ReloadOnSourceChanges

val webJars = Seq(
  "org.webjars" %% "webjars-play" % "2.8.0-1",
  "org.webjars"  % "jquery"       % "3.5.1",
  "org.webjars"  % "bootstrap"    % "4.5.3"
)

val databaseDeps = Seq(
  jdbc,
  evolutions,
  "org.playframework.anorm" %% "anorm" % "2.6.8",
  "com.h2database"           % "h2"    % "1.4.200"
)

val testLibs = Seq(
  "org.mockito" % "mockito-core" % "3.6.28",
  specs2
).map(_ % Test)

libraryDependencies ++= Seq(
  guice,
  caffeine,
  "com.amazonaws"      % "aws-java-sdk" % "1.11.916",
  "com.typesafe.play" %% "play-mailer"  % "8.0.1",
  "postgresql"         % "postgresql"   % "9.1-901-1.jdbc4" % Runtime
) ++ webJars ++ databaseDeps ++ testLibs

TwirlKeys.templateImports ++= Seq(
  "models.dao._",
  "models.images._"
)

// to avoid checking play generated files
wartremoverExcluded += target.value

wartremoverErrors ++= Warts.allBut(
  Wart.Any,
  Wart.Nothing,
  Wart.ImplicitParameter,
  Wart.Overloading,
  Wart.Equals,
  Wart.MutableDataStructures,
  Wart.StringPlusAny,
  Wart.DefaultArguments,
  Wart.Recursion
)
