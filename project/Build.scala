import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "lojinha"
  val appVersion      = "1.0-SNAPSHOT"
  val scalaVersion    = "2.10.4"

  val appDependencies = Seq(
    cache, jdbc, anorm,
    "com.amazonaws" % "aws-java-sdk" % "1.9.17",
    "com.typesafe" %% "play-plugins-mailer" % "2.2.0",
    "postgresql" % "postgresql" % "9.1-901.jdbc4" % "runtime",
    "org.mockito" % "mockito-core" % "1.9.0" % "test"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    templatesImport ++= Seq(
      "models.dao._"
    ),
    
    resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"
  )
}
