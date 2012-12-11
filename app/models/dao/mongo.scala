package models.dao

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObject
import com.novus.salat.dao.SalatDAO
import com.novus.salat.global._
import play.api.Play.current
import se.radley.plugin.salat._

case class Sequence(id: ObjectId, name: String, lastValue: Int)
object Sequence {
  val dao = new SalatDAO[Sequence, ObjectId](collection = mongoCollection("sequences")) {}

  def nextIntFor(name: String): Int = {
    val mongoObj = MongoDBObject("name" -> name)
    val value = dao.findOne(mongoObj).map(_.lastValue).getOrElse(0)
    dao.update(mongoObj, MongoDBObject("name" -> name, "lastValue" -> (value + 1)), true, false)

    value
  }
}

object MongoItemDAO extends ItemDAO {
  val dao = new SalatDAO[Item, Int](collection = mongoCollection("items")) {}

  def create(name: String, description: String, imageKeys: Option[String], cat: Category) =
    dao.insert(Item(Sequence.nextIntFor("items"), name, description, imageKeys, cat))

  def sell(id: Int): Option[Item] = throw new UnsupportedOperationException("not implemented!!")

  def findById(id: Int): Option[Item] = dao.findOne(MongoDBObject("_id" -> id))

  def all(sold: Boolean): List[Item] = dao.find(MongoDBObject("sold" -> sold)).toList

  def all(cat: Category, sold: Boolean): List[Item] = dao.find(MongoDBObject("category" -> cat, "sold" -> sold)).toList

  def delete(id: Long) = {}
}

//TODO: the current mapping causes items to be duplicated inside the bids
// how to fix this, yet keeping the abstraction that supports both SQL and NoSQL ?
object MongoBidDAO extends BidDAO {
  val dao = new SalatDAO[Bid, Int](collection = mongoCollection("bids")) {}

  def all(itemId: Int): List[Bid] = dao.find(MongoDBObject("item._id" -> itemId)).toList

  def highest(itemId: Int): Option[Bid] = dao.find(
    MongoDBObject("item._id" -> itemId)).foldLeft[Option[Bid]](None)((res, cur) => res match {
      case None => Some(cur)
      case Some(bid) => if (bid.value > cur.value) Some(bid) else Some(cur)
    }
  )

  def create(bid: Bid) = dao.insert(bid.copy(id = Sequence.nextIntFor("bids")))
}
