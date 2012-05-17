import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

  val appName         = "lojinha"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    "org.mockito" % "mockito-core" % "1.9.0" % "test"
  )

  val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
    // Add your own project settings here      
  )
}
