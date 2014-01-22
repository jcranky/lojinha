
import play.api._
import models.dao.anorm._

object Global extends GlobalSettings {
  override def onStart(app: Application) =
    if (app.mode == Mode.Dev) DevData.insert()
}

object DevData {
  def insert() = {
    Seq(
      "Games" -> "games", "Books" -> "books", "CDs" -> "cds").foreach {
        case (displayName, urlName) => AnormCategoryDAO.create(displayName, urlName)
      }
    val gamesCat = AnormCategoryDAO.findByName("games").get
    val booksCat = AnormCategoryDAO.findByName("books").get
    val cdsCat = AnormCategoryDAO.findByName("cds").get

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
      ("Black Album", "Black Album, A Metallica CD", 19, None, cdsCat)).foreach {
        case (name, description, minValue, imgs, cat) => AnormItemDAO.create(name, description, minValue, imgs, cat)
      }
  }
}
