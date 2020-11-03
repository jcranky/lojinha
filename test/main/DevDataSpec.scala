package main

import helpers.ApplicationWithDAOs
import org.specs2.mutable.Specification

@SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements"))
class DevDataSpec extends Specification {

  "DevData" should {
    "create all test items" in new ApplicationWithDAOs {
      val devData: DevData = new DevData(itemDAO, categoryDAO)
      devData.ensureData() must_== 11
    }

    "be a noop if test data is already present in Dev environments" in new ApplicationWithDAOs {
      val devData: DevData = new DevData(itemDAO, categoryDAO)
      devData.ensureData()

      devData.ensureData() must_== 0
    }
  }
}
