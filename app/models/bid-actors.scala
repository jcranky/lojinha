package models

import akka.actor._
import javax.inject.{Inject, Singleton}
import models.dao._

@Singleton
class BidHelper @Inject() (system: ActorSystem, itemDAO: ItemDAO, bidDAO: BidDAO, emailActorHelper: EMailActorHelper) {
  val masterBidActor: ActorRef =
    system.actorOf(Props(new MasterBidActor(itemDAO, bidDAO, emailActorHelper)), "master-bid-actor")

  def processBid(email: String, value: BigDecimal, notifyBetterBids: Boolean, itemId: Int, itemUrl: String) = {
    masterBidActor ! ProcessBid(email, value, notifyBetterBids, itemId, itemUrl)
  }
}

class MasterBidActor(itemDAO: ItemDAO, bidDAO: BidDAO, emailActorHelper: EMailActorHelper) extends Actor {
  var bidProcessors = Map.empty[Int, ActorRef]

  def receive = {
    case p @ ProcessBid(email, value, notifyBetterBids, itemId, itemUrl) =>
      bidProcessors.getOrElse(itemId, newBidProcessActor(itemId)) ! p
  }

  def newBidProcessActor(itemId: Int) = {
    val newBidProcessActor = context.actorOf(Props(new BidProcessActor(itemId, itemDAO, bidDAO, emailActorHelper)))
    bidProcessors += (itemId -> newBidProcessActor)

    newBidProcessActor
  }
}

class BidProcessActor(itemId: Int, itemDAO: ItemDAO, bidDAO: BidDAO, emailActorHelper: EMailActorHelper) extends Actor {
  // fixme: inject this instead?
  val bidProcessor = new BidProcessor(itemId, itemDAO, bidDAO)

  def receive = {
    case ProcessBid(email, value, notifyBetterBids, _, itemUrl) =>
      emailActorHelper.actor ! BidReceivedMessage(bidProcessor.item.name, itemUrl, email)

      bidProcessor.itemBids.bidsList.lastOption.foreach { bid => if (bid.notifyBetterBids)
        emailActorHelper.actor ! BidToppedMessage(bidProcessor.item.name, itemUrl, bid.bidderEmail)
      }
      
      bidProcessor.addBid(Bid(email, value, notifyBetterBids, bidProcessor.item))
  }
}

class BidProcessor(itemId: Int, itemDAO: ItemDAO, bidDAO: BidDAO) {
  val item: Item = itemDAO.findById(itemId).getOrElse(
    throw new IllegalArgumentException("cannot have a BidProcessActor for an invalid Item"))

  var itemBids = new ItemBids(bidDAO.all(itemId), item)

  def addBid(bid: Bid) = if (!itemBids.higherBid.exists(_.value > bid.value)) {
    itemBids = itemBids.withBid(bid)
    bidDAO.create(bid)
  }
}

// messages
case class ProcessBid(email: String, value: BigDecimal, notifyBetterBids: Boolean, itemId: Int, itemUrl: String)
