package models.dao

import com.google.inject.ImplementedBy
import models.dao.anorm.{ AnormBidDAO, AnormCategoryDAO, AnormItemDAO }
import org.joda.time.DateTime

final case class Category(id: Int, displayName: String, urlName: String)

@SuppressWarnings(Array("org.wartremover.warts.FinalCaseClass"))
case class Item(
  id: Int,
  name: String,
  description: String,
  minValue: BigDecimal,
  imageKeys: Option[String],
  cat: Category,
  createdDate: DateTime = new DateTime(),
  sold: Boolean = false
)

final case class Bid(
  id: Int,
  bidderEmail: String,
  value: BigDecimal,
  dateTime: DateTime,
  notifyBetterBids: Boolean,
  item: Item
) extends Ordered[Bid] {

  def compare(otherBid: Bid): Int = (value - otherBid.value).toInt
}

object Bid {

  def apply(bidderEmail: String, value: BigDecimal, notifyBetterBids: Boolean, item: Item): Bid =
    Bid(0, bidderEmail, value, new DateTime, notifyBetterBids, item)
}

@ImplementedBy(classOf[AnormCategoryDAO])
trait CategoryDAO {
  def create(displayName: String, urlName: String): Int

  def findById(id: Int): Option[Category]
  def findByName(name: String): Option[Category]

  def all(): List[Category]
}

@ImplementedBy(classOf[AnormItemDAO])
trait ItemDAO {
  def create(name: String, description: String, minValue: BigDecimal, imageKeys: Option[String], cat: Category): Int

  def sell(id: Int): Option[Item]

  def findById(id: Int): Option[Item]

  def findByName(name: String): Option[Item]

  def all(sold: Boolean): List[Item]

  def all(cat: Category, sold: Boolean): List[Item]

  def delete(id: Long): Int
}

@ImplementedBy(classOf[AnormBidDAO])
trait BidDAO {
  def create(bid: Bid): Int

  def all(itemId: Int): List[Bid]

  def highest(itemId: Int): Option[Bid]
}
