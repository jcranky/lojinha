package controllers

import helpers.ApplicationWithDAOs
import play.api.test._

@SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements", "org.wartremover.warts.OptionPartial"))
class ItemsSpec extends PlaySpecification {

  "the Items controller" should {
    "not have the sold and delete buttons for not logged in users" in new ApplicationWithDAOs() {
      running(app) {
        categoryDAO.create("Books", "books")
        itemDAO.create("effective java", "book description", 0, None, categoryDAO.findByName("books").get)

        val Some(result) = route(app, FakeRequest(GET, "/items/1"))

        status(result) must equalTo(OK)
        contentAsString(result) must not contain "soldButton"
        contentAsString(result) must not contain "deleteButton"
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
