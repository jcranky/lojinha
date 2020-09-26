name := "lojinha"

version := "1.0-SNAPSHOT"

lazy val root =
  (project in file("."))
    .enablePlugins(PlayScala)

scalaVersion := "2.11.12"

routesGenerator := InjectedRoutesGenerator

scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked",
  "-language:postfixOps",
  "-language:implicitConversions",
  "-Ywarn-unused",
// fixme: cannot be used for now because lots of erros pop up in routes and templates
//  "-Ywarn-unused-import",
  "-Xfatal-warnings",
  "-Xlint"
)

val webJars = Seq(
  "org.webjars"       %% "webjars-play" % "2.4.0-2",
  "org.webjars"       %  "jquery"       % "2.2.4",
  "org.webjars"       %  "bootstrap"    % "3.4.1"
)

val testLibs = Seq(
  "org.mockito" % "mockito-core" % "3.5.11",
  specs2
).map(_ % Test)

libraryDependencies ++= Seq(
  cache,
  jdbc,
  evolutions,
  "com.amazonaws"     %  "aws-java-sdk" % "1.11.870",
  "com.typesafe.play" %% "anorm"        % "2.5.3",
  "com.typesafe.play" %% "play-mailer"  % "4.0.0",
  "postgresql"        %  "postgresql"   % "9.1-901.jdbc4" % Runtime
) ++ webJars ++ testLibs

TwirlKeys.templateImports ++= Seq(
  "models.dao._"
)
