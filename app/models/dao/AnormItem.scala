package models.dao

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current

object AnormItemDAO extends ItemDAO {
  val item = {
    int("id")~str("name")~str("description")~get[Option[String]]("imageKeys") map {
      case id~name~description~picturePath => Item(id, name, description, picturePath)
    }
  }
  
  def findById(id: Int): Option[Item] = DB.withConnection { implicit c =>
    SQL("SELECT * FROM item WHERE id = {id}").on('id -> id).as(item singleOpt)
  }
  
  def all(): List[Item] = DB.withConnection { implicit c =>
    SQL("SELECT * FROM item").as(item *)
  }
  
  def all(cat: String): List[Item] = DB.withConnection { implicit c =>
    SQL("SELECT * FROM item WHERE category = {cat}").on('cat -> cat).as(item *)
  }
  
  def create(name: String, description: String, imageKeys: Option[String]) = DB.withConnection { implicit c =>
    SQL("INSERT INTO item(name, description, imageKeys) VALUES({name}, {description}, {imageKeys})").on(
      'name -> name, 'description -> description, 'imageKeys -> imageKeys).executeUpdate()
  }
  
  def delete(id: Long) = {}
}

object AnormBidDAO extends BidDAO {
  val itemDAO = DAOFactory.itemDAO
  val bid = {
    int("id")~str("bidder_email")~get[java.math.BigDecimal]("value")~int("item_id") map {
      //TODO: handle the orElse scenario on the item finding
      case id~emailBidder~value~itemId => Bid(id, emailBidder, value, itemDAO.findById(itemId).get)
    }
  }
  
  def all(itemId: Int): List[Bid] = DB.withConnection { implicit c =>
    SQL("SELECT * FROM bid WHERE item_id = {itemId}").on('itemId -> itemId).as(bid *)
  }
  
  def highest(itemId: Int): Option[Bid] = DB.withConnection { implicit c =>
    SQL("SELECT * FROM bid WHERE item_id = {itemId} AND value = (SELECT max(value) FROM bid where item_id = {itemId})").on(
      'itemId -> itemId).as(bid singleOpt)
  }
  
  def create(bid: Bid) = DB.withConnection { implicit c =>
    SQL("INSERT INTO bid(bidder_email, value, item_id) VALUES({bidderEmail}, {value}, {itemId})").on(
      'bidderEmail -> bid.bidderEmail, 'value -> bid.value.bigDecimal, 'itemId -> bid.item.id).executeUpdate()
  }
}
