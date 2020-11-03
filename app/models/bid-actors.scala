package models

import akka.actor._
import javax.inject.{Inject, Singleton}
import models.dao._

@Singleton
class BidHelper @Inject() (system: ActorSystem, itemDAO: ItemDAO, bidDAO: BidDAO, emailActorHelper: EMailActorHelper) {
  val masterBidActor: ActorRef =
    system.actorOf(Props(new MasterBidActor(itemDAO, bidDAO, emailActorHelper)), "master-bid-actor")

  def processBid(email: String, value: BigDecimal, notifyBetterBids: Boolean, itemId: Int, itemUrl: String): Unit = {
    masterBidActor ! ProcessBid(email, value, notifyBetterBids, itemId, itemUrl)
  }
}

@SuppressWarnings(Array("org.wartremover.warts.Var"))
class MasterBidActor(itemDAO: ItemDAO, bidDAO: BidDAO, emailActorHelper: EMailActorHelper) extends Actor {
  private var bidProcessors = Map.empty[Int, ActorRef]

  def receive = {
    case p @ ProcessBid(email, value, notifyBetterBids, itemId, itemUrl) =>
      bidProcessors.getOrElse(itemId, newBidProcessActor(itemId)) ! p
  }

  def newBidProcessActor(itemId: Int): ActorRef = {
    val newBidProcessActor = context.actorOf(Props(new BidProcessActor(itemId, itemDAO, bidDAO, emailActorHelper)))
    bidProcessors += (itemId -> newBidProcessActor)

    newBidProcessActor
  }
}

class BidProcessActor(itemId: Int, itemDAO: ItemDAO, bidDAO: BidDAO, emailActorHelper: EMailActorHelper) extends Actor {
  // fixme: inject this instead?
  private val bidProcessor = new BidProcessor(itemId, itemDAO, bidDAO)

  @SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements"))
  def receive = {
    case ProcessBid(email, value, notifyBetterBids, _, itemUrl) =>
      emailActorHelper.actor ! BidReceivedMessage(bidProcessor.item.name, itemUrl, email)

      bidProcessor.itemBids.bidsList.lastOption.foreach { bid => if (bid.notifyBetterBids)
        emailActorHelper.actor ! BidToppedMessage(bidProcessor.item.name, itemUrl, bid.bidderEmail)
      }
      
      bidProcessor.addBid(Bid(email, value, notifyBetterBids, bidProcessor.item))
  }
}

@SuppressWarnings(Array("org.wartremover.warts.Throw", "org.wartremover.warts.Var", "org.wartremover.warts.AnyVal"))
class BidProcessor(itemId: Int, itemDAO: ItemDAO, bidDAO: BidDAO) {
  val item: Item = itemDAO.findById(itemId).getOrElse(
    throw new IllegalArgumentException("cannot have a BidProcessActor for an invalid Item"))

  var itemBids: ItemBids = new ItemBids(bidDAO.all(itemId), item)

  // fixme: use a return type that actually makes sense
  def addBid(bid: Bid): AnyVal = if (!itemBids.higherBid.exists(_.value > bid.value)) {
    itemBids = itemBids.withBid(bid)
    bidDAO.create(bid)
  }
}

// messages
final case class ProcessBid(email: String, value: BigDecimal, notifyBetterBids: Boolean, itemId: Int, itemUrl: String)
