package models

import org.specs2.mutable.Specification
import play.api.test._
import play.api.test.Helpers._

class BidProcessorSpec extends Specification {
  "a bid processor" should {
    "throw exception if its controlled item doesn't exist" in {
      running(FakeApplication()) {
        new BidProcessor(-1) must throwAn[IllegalArgumentException]
      }
    }
  }
}
