package main

import javax.inject.{Inject, Singleton}
import models.dao.{CategoryDAO, ItemDAO}
import play.api._

@Singleton
class Init @Inject()(environment: Environment, itemDAO: ItemDAO, categoryDAO: CategoryDAO) {
  if (environment.mode == Mode.Dev)
    new DevData(itemDAO, categoryDAO).ensureData()
}

class DevData(itemDAO: ItemDAO, categoryDAO: CategoryDAO) {
  def ensureData(): Int = {
    val mario64 = itemDAO.findByName("Mario 64")
    mario64 match {
      case Some(_) => 0
      case None => createData()
    }
  }

  private def createData(): Int = {
    Seq(
      "Games" -> "games", "Books" -> "books", "CDs" -> "cds").foreach {
        case (displayName, urlName) => categoryDAO.create(displayName, urlName)
    }
    val gamesCat = categoryDAO.findByName("games").get
    val booksCat = categoryDAO.findByName("books").get
    val cdsCat = categoryDAO.findByName("cds").get

    Seq(
      ("Mario 64", "Mario 64 para Nintendo 64", 80, None, gamesCat),
      ("Zelda 64", "Zelda 64 para Nintendo 64", 90, None, gamesCat),
      ("Lord of the Rings", "Lord of the Rings book", 22, None, booksCat),
      ("The Hobbit", "The Hobbit book", 20, None, booksCat),
      ("Game of Thrones", "Game of Thrones book", 20, None, booksCat),
      ("The Number of the Beast", "The Number of the Beast, An Iron Maiden CD", 21, None, cdsCat),
      ("Fear of the Dark", "Fear of the Dark, An Iron Maiden CD", 20, None, cdsCat),
      ("Live in Athens", "Live in Athens, An Iced Earth CD", 30, None, cdsCat),
      ("Horror Show", "Horror Show, An Iced Earth CD", 20, None, cdsCat),
      ("Fuel", "Fuel, A Metallica CD", 15, None, cdsCat),
      ("Black Album", "Black Album, A Metallica CD", 19, None, cdsCat)).map {
        case (name, description, minValue, imgs, cat) => itemDAO.create(name, description, minValue, imgs, cat)
    }.sum
  }
}
