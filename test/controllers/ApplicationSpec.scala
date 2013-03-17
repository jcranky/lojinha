package controllers

import org.specs2.mutable.Specification
import play.api.test._
import play.api.test.Helpers._

class ApplicationSpec extends Specification {
  "the application main controller" should {
    "return the about page in portuguese as default" in {
      running(FakeApplication()) {
        val Some(result) = route(FakeRequest(GET, "/about"))

        status(result) must equalTo(OK)
        contentAsString(result) must not contain("mini-store")
      }
    }

    "return the about page in english" in {
      running(FakeApplication()) {
        val Some(result) = route(FakeRequest(GET, "/about").withHeaders("Accept-Language" -> "en"))

        status(result) must equalTo(OK)
        contentAsString(result) must contain("mini-store")
      }
    }

    "return the about page in portuguese when the pt header is present" in {
      running(FakeApplication()) {
        val Some(result) = route(FakeRequest(GET, "/about").withHeaders("Accept-Language" -> "en,pt-BR"))

        status(result) must equalTo(OK)
        contentAsString(result) must not contain("mini-store")
      }
    }
  }
}
