import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

  val appName         = "lojinha"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    "org.mockito" % "mockito-core" % "1.9.0" % "test",
    "se.radley" %% "play-plugins-salat" % "1.0.3"
  )

  val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
    routesImport += "se.radley.plugin.salat.Binders._",
    templatesImport += "org.bson.types.ObjectId"
  )
}
