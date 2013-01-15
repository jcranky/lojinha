import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

  val appName         = "lojinha"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    "com.amazonaws" % "aws-java-sdk" % "1.3.27",
    "postgresql" % "postgresql" % "9.1-901.jdbc4" % "runtime",
    "org.mockito" % "mockito-core" % "1.9.0" % "test"
  ) map (_.withSources)

  val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
    templatesImport ++= Seq(
      "models.dao._"
    )
  )
}
