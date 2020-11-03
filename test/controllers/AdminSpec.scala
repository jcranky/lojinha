package controllers

import helpers.ApplicationWithDAOs
import org.specs2.mutable.Specification
import play.api.test.Helpers._
import play.api.test._

@SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements", "org.wartremover.warts.OptionPartial"))
class AdminSpec extends Specification {

  "the Admin controller handling item sold status" should {
    "return 404 if the item sold doesn't exist" in new ApplicationWithDAOs() {
      running(app) {
        val Some(result) = route(app, FakeRequest(POST, "/admin/items/99/sold").withSession("email" -> "admin@lojinha.com"))

        status(result) must equalTo(NOT_FOUND)
      }
    }

    "return to the item details page without showing the bid form for a valid item sold click" in new ApplicationWithDAOs() {
      running(app) {
        categoryDAO.create("Books", "books")
        itemDAO.create("effective java", "book description", 0, None, categoryDAO.findByName("books").get)

        val Some(result) = route(app, FakeRequest(POST, "/admin/items/1/sold").withSession("email" -> "admin@lojinha.com"))

        status(result) must equalTo(OK)
        contentAsString(result) must not contain ("newBidForm")
        contentAsString(result) must not contain ("soldButton")
      }
    }
  }
}
