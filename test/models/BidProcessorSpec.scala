package models

import helpers.ApplicationWithDAOs
import play.api.test._

@SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements"))
class BidProcessorSpec extends PlaySpecification {
  sequential

  "a bid processor" should {
    "throw exception if its controlled item doesn't exist" in new ApplicationWithDAOs() {

      running(app) {
        new BidProcessor(-1, itemDAO, bidDAO) must throwAn[IllegalArgumentException]
      }
    }
  }
}
