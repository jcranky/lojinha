package models

import akka.actor._
import play.api.Play.current
import play.api.libs.concurrent.Akka

import models.dao._

object BidHelper {
  val masterBidActor = Akka.system.actorOf(Props[MasterBidActor], "master-bid-actor")

  def processBid(email: String, value: BigDecimal, notifyBetterBids: Boolean, itemId: Int, itemUrl: String) = {
    masterBidActor ! ProcessBid(email, value, notifyBetterBids, itemId, itemUrl)
  }
}

class MasterBidActor extends Actor {
  var bidProcessors = Map.empty[Int, ActorRef]

  def receive = {
    case p @ ProcessBid(email, value, notifyBetterBids, itemId, itemUrl) =>
      bidProcessors.getOrElse(itemId, newBidProcessActor(itemId)) ! p
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
    case ProcessBid(email, value, notifyBetterBids, _, itemUrl) =>
      EMailActor.actor ! BidReceivedMessage(bidProcessor.item.name, itemUrl, email)
      bidProcessor.itemBids.bidsList.lastOption.foreach { bid => if (bid.notifyBetterBids)
        EMailActor.actor ! BidToppedMessage(bidProcessor.item.name, itemUrl, bid.bidderEmail)
      }
      
      bidProcessor.addBid(Bid(email, value, notifyBetterBids, bidProcessor.item))
  }
}

class BidProcessor(itemId: Int) {
  val itemDAO = DAOFactory.itemDAO
  val bidDAO = DAOFactory.bidDAO

  val item = itemDAO.findById(itemId).getOrElse(
    throw new IllegalArgumentException("cannot have a BidProcessActor for an inexistent Item"))
  var itemBids = new ItemBids(bidDAO.all(itemId), item)

  def addBid(bid: Bid) = if (itemBids.higherBid.filter(_.value > bid.value).isEmpty) {
    itemBids = itemBids.withBid(bid)
    bidDAO.create(bid)
  }
}

// messages
case class ProcessBid(email: String, value: BigDecimal, notifyBetterBids: Boolean, itemId: Int, itemUrl: String)
