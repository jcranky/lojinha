package helpers

import models.dao.{BidDAO, CategoryDAO, ItemDAO}
import org.specs2.specification.Scope
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers.inMemoryDatabase
import play.api.{Application, Mode}

abstract class ApplicationWithDAOs(additionalConfigs: Map[String, String] = Map.empty) extends Scope {
  val app: Application =
    GuiceApplicationBuilder()
      .configure(inMemoryDatabase() ++ additionalConfigs)
      .in(Mode.Test)
      .build()

  val categoryDAO: CategoryDAO = app.injector.instanceOf[CategoryDAO]
  val itemDAO: ItemDAO = app.injector.instanceOf[ItemDAO]
  val bidDAO: BidDAO = app.injector.instanceOf[BidDAO]
}
