package controllers

import helpers.ApplicationWithDAOs
import org.specs2.mutable.Specification
import play.api.test.Helpers._
import play.api.test._

class ItemsSpec extends Specification {

  "the Items controller" should {
    "not have the sold and delete buttons for not logged in users" in new ApplicationWithDAOs() {
      running(app) {
        categoryDAO.create("Books", "books")
        itemDAO.create("effective java", "book description", 0, None, categoryDAO.findByName("books").get)

        val Some(result) = route(app, FakeRequest(GET, "/items/1"))

        status(result) must equalTo(OK)
        contentAsString(result) must not contain ("soldButton")
        contentAsString(result) must not contain ("deleteButton")
      }
    }

    "return NotFound for a deleted item" in new ApplicationWithDAOs() {
      running(app) {
        categoryDAO.create("Books", "books")
        itemDAO.create("effective java", "book description", 0, None, categoryDAO.findByName("books").get)
        itemDAO.delete(1)

        val Some(result) = route(app, FakeRequest(GET, "/items/1"))

        status(result) must equalTo(NOT_FOUND)
      }
    }
  }
}
