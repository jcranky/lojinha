package controllers

import models.dao.DAOFactory._
import org.specs2.mutable.Specification
import play.api.test._
import play.api.test.Helpers._

class ItemsSpec extends Specification {
  "the Items controller" should {
    "not have the mark as sold button for not logged in users" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        itemDAO.create("effective java", "book description", None, categoryDAO.getByName("Books"))

        val Some(result) = routeAndCall(FakeRequest(GET, "/items/1"))

        status(result) must equalTo(OK)
        contentAsString(result) must not contain("soldButton")
      }
    }
  }
}
