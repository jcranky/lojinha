package models.dao

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import org.joda.time.DateTime
import play.api.Play.current

object AnormCategoryDAO extends CategoryDAO {
  val category = {
    int("id") ~ str("name") map {
      case id~name => Category(id, name)
    }
  }

  def create(name: String) = DB.withConnection { implicit c =>
    SQL("INSERT INTO category(name) VALUES({name})").on('name -> name).executeUpdate()
  }

  def findById(id: Int): Option[Category] = DB.withConnection { implicit c =>
    SQL("SELECT * FROM category WHERE id = {id}").on('id -> id).as(category singleOpt)
  }

  def findByName(name: String): Option[Category] = DB.withConnection { implicit c =>
    SQL("SELECT * FROM category WHERE name = {name}").on('name -> name).as(category singleOpt)
  }

  def getByName(name: String): Category = DB.withConnection { implicit c =>
    findByName(name).getOrElse {
      create(name)
      findByName(name).get
    }
  }

  def all(): List[Category] = DB.withConnection { implicit c =>
    SQL("SELECT * FROM category").as(category *)
  }
}

object AnormItemDAO extends ItemDAO {
  val categoryDAO = DAOFactory.categoryDAO

  val item = {
    int("id") ~ str("name") ~ str("description") ~ get[Option[String]]("imageKeys") ~ int("category_id") ~ bool("sold") map {
      case id~name~description~picturePath~catId~sold => Item(id, name, description, picturePath, categoryDAO.findById(catId).get, sold)
    }
  }

  def create(name: String, description: String, imageKeys: Option[String], cat: Category) = DB.withConnection { implicit c =>
    SQL("INSERT INTO item(name, description, imageKeys, category_id) VALUES({name}, {description}, {imageKeys}, {catId})").on(
      'name -> name, 'description -> description, 'imageKeys -> imageKeys, 'catId -> cat.id).executeUpdate()
  }

  def sell(id: Int): Option[Item] = DB.withConnection { implicit c =>
    SQL("UPDATE item SET sold = true WHERE id = {id}").on('id -> id).executeUpdate()
    findById(id)
  }

  def findById(id: Int): Option[Item] = DB.withConnection { implicit c =>
    SQL("SELECT * FROM item WHERE id = {id} AND deleted = false").on('id -> id).as(item singleOpt)
  }

  def all(): List[Item] = DB.withConnection { implicit c =>
    SQL("SELECT * FROM item WHERE deleted = false ORDER BY sold").as(item *)
  }

  def all(cat: Category): List[Item] = DB.withConnection { implicit c =>
    SQL("SELECT * FROM item WHERE category_id = {catId} AND deleted = false ORDER BY sold").on('catId -> cat.id).as(item *)
  }

  def delete(id: Long): Unit = DB.withConnection { implicit c =>
    SQL("UPDATE item SET deleted = true WHERE id = {id}").on('id -> id).executeUpdate()
  }
}

object AnormBidDAO extends BidDAO {
  val itemDAO = DAOFactory.itemDAO
  val bid = {
    int("id") ~ str("bidder_email") ~ get[java.math.BigDecimal]("value") ~ date("dateTime") ~ int("item_id") map {
      case id~emailBidder~value~dateTime~itemId => Bid(id, emailBidder, value, new DateTime(dateTime), itemDAO.findById(itemId).get)
    }
  }

  def create(bid: Bid) = DB.withConnection { implicit c =>
    SQL("INSERT INTO bid(bidder_email, value, dateTime, item_id) VALUES({bidderEmail}, {value}, {dateTime}, {itemId})").on(
      'bidderEmail -> bid.bidderEmail, 'value -> bid.value.bigDecimal, 'dateTime -> bid.dateTime.toDate, 'itemId -> bid.item.id).executeUpdate()
  }

  def all(itemId: Int): List[Bid] = DB.withConnection { implicit c =>
    SQL("SELECT * FROM bid WHERE item_id = {itemId}").on('itemId -> itemId).as(bid *)
  }

  def highest(itemId: Int): Option[Bid] = DB.withConnection { implicit c =>
    SQL("SELECT * FROM bid WHERE item_id = {itemId} AND value = (SELECT max(value) FROM bid where item_id = {itemId})").on(
      'itemId -> itemId).as(bid singleOpt)
  }
}

object AnormUserDAO extends UserDAO {
  val user = {
    int("id") ~ str("email") ~ str("name") ~ str("passwd") map {
      case id~email~name~passwd => User(id, email, name, passwd)
    }
  }

  def authenticate(email: String, passwd: String): Option[User] = DB.withConnection { implicit c =>
    SQL("SELECT * FROM _user WHERE email = {email} AND passwd = {passwd}").on('email -> email, 'passwd -> passwd).as(user singleOpt)
  }

  def findByEmail(email: String): Option[User] = DB.withConnection { implicit c =>
    SQL("SELECT * FROM _user WHERE email = {email}").on('email -> email).as(user singleOpt)
  }

  def changePassword(email: String, newPasswd: String) = DB.withConnection { implicit c =>
    SQL("UPDATE _user set passwd = {password} WHERE email = {email}").on(
      'password -> newPasswd, 'email -> email).executeUpdate()
  }
}
