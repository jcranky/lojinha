package controllers

import models.dao.DAOFactory._
import org.specs2.mutable.Specification
import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._

class AdminSpec extends Specification {
  // both methods below were copied from:
  // https://github.com/maffoo/play-test-security/blob/master/test/ApplicationTest.scala

  def routeAndCallImproved[T](request: FakeRequest[T]): Option[Result] = {
    routeAndCallImproved(this.getClass.getClassLoader.loadClass("Routes").asInstanceOf[Class[play.core.Router.Routes]], request)
  }

  def routeAndCallImproved[T, ROUTER <: play.core.Router.Routes](router: Class[ROUTER], request: FakeRequest[T]): Option[Result] = {
    val routes = router.getClassLoader.loadClass(router.getName + "$").getDeclaredField("MODULE$").get(null).asInstanceOf[play.core.Router.Routes]
    routes.routes.lift(request).map {
      case action: Action[_] =>
        action.parser(request).run.await.get match {
          case Left(result) => result
          case Right(parsedBody) =>
            action(request.map(_ => parsedBody))
        }
    }
  }

  "the Admin controller handling item sold status" should {
    "return 404 if the item sold doesn't exist" in {
      running(FakeApplication()) {
        val Some(result) = routeAndCallImproved(FakeRequest(POST, "/admin/items/99/sold").withSession("email" -> "admin@lojinha.com"))

        status(result) must equalTo(NOT_FOUND)
      }
    }

    "return to the item details page without showing the bid form for a valid item sold click" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        categoryDAO.create("Books", "books")
        itemDAO.create("effective java", "book description", None, categoryDAO.findByName("books").get)

        val Some(result) = routeAndCallImproved(FakeRequest(POST, "/admin/items/1/sold").withSession("email" -> "admin@lojinha.com"))

        status(result) must equalTo(OK)
        contentAsString(result) must not contain("newBidForm")
        contentAsString(result) must not contain("soldButton")
      }
    }
  }
}
