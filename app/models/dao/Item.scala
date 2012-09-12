package models.dao

case class Category(id: Int, name: String)

case class Item(id: Int, name: String, description: String, imageKeys: Option[String], cat: Category)

case class Bid(id: Int, bidderEmail: String, value: BigDecimal, item: Item) extends Ordered[Bid] {
  def compare(otherBid: Bid) = (value - otherBid.value).toInt
}
object Bid {
  def apply(bidderEmail: String, value: BigDecimal, item: Item): Bid = Bid(0, bidderEmail, value, item)
}

trait CategoryDAO {
  def create(name: String)

  def findById(id: Int): Option[Category]
  def findByName(name: String): Option[Category]

  def getByName(name: String): Category
}

trait ItemDAO {
  def findById(id: Int): Option[Item]

  def all(): List[Item]

  def all(cat: String): List[Item]

  def create(name: String, description: String, imageKeys: Option[String], cat: Category)

  def delete(id: Long)
}

trait BidDAO {
  def all(itemId: Int): List[Bid]

  def highest(itemId: Int): Option[Bid]

  def create(bid: Bid)
}
