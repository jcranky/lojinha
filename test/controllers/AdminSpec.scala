package controllers

import models.dao.DAOFactory._
import org.specs2.mutable.Specification
import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._

class AdminSpec extends Specification {
  "the Admin controller handling item sold status" should {
    "return 404 if the item sold doesn't exist" in {
      running(FakeApplication()) {
        val Some(result) = route(FakeRequest(POST, "/admin/items/99/sold").withSession("email" -> "admin@lojinha.com"))

        status(result) must equalTo(NOT_FOUND)
      }
    }

    "return to the item details page without showing the bid form for a valid item sold click" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        categoryDAO.create("Books", "books")
        itemDAO.create("effective java", "book description", None, categoryDAO.findByName("books").get)

        val Some(result) = route(FakeRequest(POST, "/admin/items/1/sold").withSession("email" -> "admin@lojinha.com"))

        status(result) must equalTo(OK)
        contentAsString(result) must not contain("newBidForm")
        contentAsString(result) must not contain("soldButton")
      }
    }
  }
}
