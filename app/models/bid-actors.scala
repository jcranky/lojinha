package models

import akka.actor._
import play.api.Play.current
import play.api.libs.concurrent.Akka

object AkkaDelegate {
  val masterBidActor = Akka.system.actorOf(Props[MasterBidActor], "master-bid-actor")
  
  def sendToAkka(email: String, value: BigDecimal, itemId: Int) = {
    masterBidActor ! ProcessBid(email, value, itemId)
  }
}

class MasterBidActor extends Actor {
  import scala.collection.mutable.HashMap
  var bidProcessors = HashMap[Int, ActorRef]()
  
  def receive = {
    case ProcessBid(email, value, itemId) =>
      bidProcessors.get(itemId).getOrElse(newBidProcessActor(itemId)) ! ProcessBid(email, value, itemId)
  }
  
  def newBidProcessActor(itemId: Int) = {
    val newBidProcessActor = context.actorOf(Props(new BidProcessActor(itemId)))
    bidProcessors += (itemId -> newBidProcessActor)
        
    newBidProcessActor
  }
}

class BidProcessActor(itemId: Int) extends Actor {
  val bidProcessor = new BidProcessor(itemId)
  
  def receive = {
    case ProcessBid(email, value, itemId) =>
      bidProcessor.addBid(Bid(0, email, value, bidProcessor.item))
  }
}

class BidProcessor(itemId: Int) {
  val item = Item.findById(itemId).getOrElse(
    throw new IllegalArgumentException("cannot have a BidProcessActor for an inexistent Item"))
  var itemBids = new ItemBids(Bid.all(itemId), item)
  
  def addBid(bid: Bid) = {
    itemBids = itemBids.withBid(bid)
    Bid.create(bid)
  }
}

// messages
case class ProcessBid(email: String, value: BigDecimal, itemId: Int)
