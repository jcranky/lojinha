package models.dao.anorm

import anorm._
import anorm.SqlParser._
import org.joda.time.DateTime
import play.api.db.DB
import play.api.Play.current
import models.dao.{Bid, Item, BidDAO, ItemDAO, Category, DAOFactory}

object AnormItemDAO extends ItemDAO {
  val categoryDAO = DAOFactory.categoryDAO

  val item = {
    int("id") ~ str("name") ~ str("description") ~ get[java.math.BigDecimal]("min_value") ~
    get[Option[String]]("imageKeys")~int("category_id")~date("created_date")~bool("sold") map {
      case id~name~description~minValue~picturePath~catId~createdDate~sold =>
        Item(id, name, description, minValue, picturePath, categoryDAO.findById(catId).get, new DateTime(createdDate), sold)
    }
  }

  def create(name: String, description: String, minValue: BigDecimal, imageKeys: Option[String], cat: Category) = DB.withConnection { implicit c =>
    SQL("INSERT INTO item(name, description, min_value, imageKeys, category_id) VALUES({name}, {description}, {minValue}, {imageKeys}, {catId})").on(
      'name -> name, 'description -> description, 'minValue -> minValue.bigDecimal, 'imageKeys -> imageKeys, 'catId -> cat.id).executeUpdate()
  }

  def sell(id: Int): Option[Item] = DB.withConnection { implicit c =>
    SQL("UPDATE item SET sold = true WHERE id = {id}").on('id -> id).executeUpdate()
    findById(id)
  }

  def findById(id: Int): Option[Item] = DB.withConnection { implicit c =>
    SQL("SELECT * FROM item WHERE id = {id} AND deleted = false").on('id -> id).as(item singleOpt)
  }

  def all(sold: Boolean): List[Item] = DB.withConnection { implicit c =>
    SQL("SELECT * FROM item WHERE deleted = false AND sold = {sold} ORDER BY created_date DESC").on('sold -> sold).as(item *)
  }

  def all(cat: Category, sold: Boolean): List[Item] = DB.withConnection { implicit c =>
    SQL("SELECT * FROM item WHERE category_id = {catId} AND deleted = false AND sold = {sold} ORDER BY created_date DESC").on('catId -> cat.id, 'sold -> sold).as(item *)
  }

  def delete(id: Long): Unit = DB.withConnection { implicit c =>
    SQL("UPDATE item SET deleted = true WHERE id = {id}").on('id -> id).executeUpdate()
  }
}

object AnormBidDAO extends BidDAO {
  val itemDAO = DAOFactory.itemDAO
  val bid = {
    int("id") ~ str("bidder_email") ~ get[java.math.BigDecimal]("value") ~ date("dateTime") ~ bool("notify_better_bids") ~ int("item_id") map {
      case id~emailBidder~value~dateTime~notifyBetterBids~itemId => Bid(id, emailBidder, value, new DateTime(dateTime), notifyBetterBids, itemDAO.findById(itemId).get)
    }
  }

  def create(bid: Bid) = DB.withConnection { implicit c =>
    SQL("INSERT INTO bid(bidder_email, value, dateTime, notify_better_bids, item_id) VALUES({bidderEmail}, {value}, {dateTime}, {notifyBetterBids}, {itemId})").on(
      'bidderEmail -> bid.bidderEmail, 'value -> bid.value.bigDecimal, 'dateTime -> bid.dateTime.toDate, 'notifyBetterBids -> bid.notifyBetterBids, 'itemId -> bid.item.id).executeUpdate()
  }

  def all(itemId: Int): List[Bid] = DB.withConnection { implicit c =>
    SQL("SELECT * FROM bid WHERE item_id = {itemId}").on('itemId -> itemId).as(bid *)
  }

  def highest(itemId: Int): Option[Bid] = DB.withConnection { implicit c =>
    SQL("SELECT * FROM bid WHERE item_id = {itemId} AND value = (SELECT max(value) FROM bid where item_id = {itemId})").on(
      'itemId -> itemId).as(bid singleOpt)
  }
}
