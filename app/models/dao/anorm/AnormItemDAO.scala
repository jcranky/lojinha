package models.dao.anorm

import anorm.SqlParser._
import anorm._
import javax.inject.{Inject, Singleton}
import models.dao._
import org.joda.time.DateTime
import play.api.db.Database

@Singleton
class AnormItemDAO @Inject() (db: Database, categoryDAO: CategoryDAO) extends ItemDAO {

  val item: RowParser[Item] = {
    int("id") ~ str("name") ~ str("description") ~ get[java.math.BigDecimal]("min_value") ~
    get[Option[String]]("imageKeys")~int("category_id")~date("created_date")~bool("sold") map {
      case id~name~description~minValue~picturePath~catId~createdDate~sold =>
        Item(id, name, description, minValue, picturePath, categoryDAO.findById(catId).get, new DateTime(createdDate), sold)
    }
  }

  def create(name: String, description: String, minValue: BigDecimal, imageKeys: Option[String], cat: Category): Unit = db.withConnection { implicit c =>
    SQL("INSERT INTO item(name, description, min_value, imageKeys, category_id) VALUES({name}, {description}, {minValue}, {imageKeys}, {catId})").on(
      'name -> name, 'description -> description, 'minValue -> minValue.bigDecimal, 'imageKeys -> imageKeys, 'catId -> cat.id).executeUpdate()
  }

  def sell(id: Int): Option[Item] = db.withConnection { implicit c =>
    SQL("UPDATE item SET sold = true WHERE id = {id}").on('id -> id).executeUpdate()
    findById(id)
  }

  def findById(id: Int): Option[Item] = db.withConnection { implicit c =>
    SQL("SELECT * FROM item WHERE id = {id} AND deleted = false").on('id -> id).as(item singleOpt)
  }

  def all(sold: Boolean): List[Item] = db.withConnection { implicit c =>
    SQL("SELECT * FROM item WHERE deleted = false AND sold = {sold} ORDER BY created_date DESC").on('sold -> sold).as(item *)
  }

  def all(cat: Category, sold: Boolean): List[Item] = db.withConnection { implicit c =>
    SQL("SELECT * FROM item WHERE category_id = {catId} AND deleted = false AND sold = {sold} ORDER BY created_date DESC").on('catId -> cat.id, 'sold -> sold).as(item *)
  }

  def delete(id: Long): Unit = db.withConnection { implicit c =>
    SQL("UPDATE item SET deleted = true WHERE id = {id}").on('id -> id).executeUpdate()
  }
}

@Singleton
class AnormBidDAO @Inject() (db: Database, itemDAO: ItemDAO) extends BidDAO {
  val bid = {
    int("id") ~ str("bidder_email") ~ get[java.math.BigDecimal]("value") ~ date("dateTime") ~ bool("notify_better_bids") ~ int("item_id") map {
      case id~emailBidder~value~dateTime~notifyBetterBids~itemId => Bid(id, emailBidder, value, new DateTime(dateTime), notifyBetterBids, itemDAO.findById(itemId).get)
    }
  }

  def create(bid: Bid) = db.withConnection { implicit c =>
    SQL("INSERT INTO bid(bidder_email, value, dateTime, notify_better_bids, item_id) VALUES({bidderEmail}, {value}, {dateTime}, {notifyBetterBids}, {itemId})").on(
      'bidderEmail -> bid.bidderEmail, 'value -> bid.value.bigDecimal, 'dateTime -> bid.dateTime.toDate, 'notifyBetterBids -> bid.notifyBetterBids, 'itemId -> bid.item.id).executeUpdate()
  }

  def all(itemId: Int): List[Bid] = db.withConnection { implicit c =>
    SQL("SELECT * FROM bid WHERE item_id = {itemId}").on('itemId -> itemId).as(bid *)
  }

  def highest(itemId: Int): Option[Bid] = db.withConnection { implicit c =>
    SQL("SELECT * FROM bid WHERE item_id = {itemId} AND value = (SELECT max(value) FROM bid where item_id = {itemId})").on(
      'itemId -> itemId).as(bid singleOpt)
  }
}
