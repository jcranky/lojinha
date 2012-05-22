package models

case class Item(id: Int, name: String, description: String, imageKeys: Option[String])

case class Bid(id: Int, bidderEmail: String, value: BigDecimal, item: Item) extends Ordered[Bid] {
  def compare(otherBid: Bid) = (value - otherBid.value).toInt
}
object Bid {
  def apply(bidderEmail: String, value: BigDecimal, item: Item): Bid = Bid(0, bidderEmail, value, item)
}

trait ItemDAO {
  def findById(id: Int): Option[Item]
  
  def all(): List[Item]
  
  def all(cat: String): List[Item]
  
  def create(name: String, description: String, imageKeys: Option[String])
  
  def delete(id: Long)
}

trait BidDAO {
  def all(itemId: Int): List[Bid]
  
  def highest(itemId: Int): Option[Bid]
  
  def create(bid: Bid)
}

object DAOFactory {
  val dbConfig = play.api.Play.current.configuration.getString("lojinha.db.type", Some(Set("sql", "mongo")))
  
  def itemDAO: ItemDAO = dbConfig match {
    case Some("mongo") => MongoItemDAO
    case Some("sql") => AnormItemDAO
    case None => AnormItemDAO
  }
  
  def bidDAO: BidDAO = dbConfig match {
    case Some("mongo") => MongoBidDAO
    case Some("sql") => AnormBidDAO
    case None => AnormBidDAO
  }
}
