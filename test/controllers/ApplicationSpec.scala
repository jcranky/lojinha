package controllers

import helpers.ApplicationWithDAOs
import org.specs2.mutable.Specification
import play.api.test._
import play.api.test.Helpers._

@SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements"))
class ApplicationSpec extends Specification {

  "the application main controller" should {
    "return the about page in portuguese as default" in new ApplicationWithDAOs() {
      running(app) {
        val Some(result) = route(app, FakeRequest(GET, "/about"))

        status(result) must equalTo(OK)
        contentAsString(result) must not contain "mini-store"
      }
    }

    "return the about page in english" in new ApplicationWithDAOs() {
      running(app) {
        val Some(result) = route(app, FakeRequest(GET, "/about").withHeaders("Accept-Language" -> "en"))

        status(result) must equalTo(OK)
        contentAsString(result) must contain("mini-store")
      }
    }

    "return the about page in portuguese when the pt header is present" in new ApplicationWithDAOs() {
      running(app) {
        val Some(result) = route(app, FakeRequest(GET, "/about").withHeaders("Accept-Language" -> "pt-BR,en"))

        status(result) must equalTo(OK)
        contentAsString(result) must not contain "mini-store"
      }
    }
  }
}
