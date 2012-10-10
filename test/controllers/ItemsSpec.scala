package controllers

import models.dao.DAOFactory._
import org.specs2.mutable.Specification
import play.api.test._
import play.api.test.Helpers._

class ItemsSpec extends Specification {
  "the Items controller" should {
    "not have the sold and delete buttons for not logged in users" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        itemDAO.create("effective java", "book description", None, categoryDAO.getByName("Books"))

        val Some(result) = routeAndCall(FakeRequest(GET, "/items/1"))

        status(result) must equalTo(OK)
        contentAsString(result) must not contain("soldButton")
        contentAsString(result) must not contain("deleteButton")
      }
    }

    "return NotFound for a deleted item" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        itemDAO.create("effective java", "book description", None, categoryDAO.getByName("Books"))
        itemDAO.delete(1)

        val Some(result) = routeAndCall(FakeRequest(GET, "/items/1"))

        status(result) must equalTo(NOT_FOUND)
      }
    }
  }
}
