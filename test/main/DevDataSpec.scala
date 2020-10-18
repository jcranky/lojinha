package main

import helpers.ApplicationWithDAOs
import org.specs2.mutable.Specification

class DevDataSpec extends Specification {

  "DevData" should {
    "create all test items" in new ApplicationWithDAOs {
      val devData = new DevData(itemDAO, categoryDAO)
      devData.ensureData() must_== 11
    }

    "be a noop if test data is already present in Dev environments" in new ApplicationWithDAOs {
      val devData = new DevData(itemDAO, categoryDAO)
      devData.ensureData()

      devData.ensureData() must_== 0
    }
  }
}
