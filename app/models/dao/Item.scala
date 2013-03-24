package models.dao

import org.joda.time.DateTime

case class Category(id: Int, displayName: String, urlName: String)

case class Item(id: Int, name: String, description: String, minValue: BigDecimal, imageKeys: Option[String],
                cat: Category, createdDate: DateTime = new DateTime(), sold: Boolean = false)

case class Bid(id: Int, bidderEmail: String, value: BigDecimal, dateTime: DateTime,
               notifyBetterBids: Boolean, item: Item) extends Ordered[Bid] {

  def compare(otherBid: Bid) = (value - otherBid.value).toInt
}

object Bid {
  def apply(bidderEmail: String, value: BigDecimal, notifyBetterBids: Boolean, item: Item): Bid =
    Bid(0, bidderEmail, value, new DateTime, notifyBetterBids, item)
}

trait CategoryDAO {
  def create(displayName: String, urlName: String)

  def findById(id: Int): Option[Category]
  def findByName(name: String): Option[Category]

  def all(): List[Category]
}

trait ItemDAO {
  def create(name: String, description: String, minValue: BigDecimal, imageKeys: Option[String], cat: Category)

  def sell(id: Int): Option[Item]

  def findById(id: Int): Option[Item]

  def all(sold: Boolean): List[Item]

  def all(cat: Category, sold: Boolean): List[Item]

  def delete(id: Long)
}

trait BidDAO {
  def create(bid: Bid)

  def all(itemId: Int): List[Bid]

  def highest(itemId: Int): Option[Bid]
}
