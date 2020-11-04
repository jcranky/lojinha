package models

import models.dao._

@SuppressWarnings(Array("org.wartremover.warts.FinalCaseClass"))
case class ItemBids(higherBid: Option[Bid], bidsList: List[Bid] = Nil)(implicit val item: Item) {

  @SuppressWarnings(Array("org.wartremover.warts.TraversableOps"))
  def this(bidsList: List[Bid], item: Item) =
    this(if (bidsList.nonEmpty) Some(bidsList.max) else None, bidsList)(item)

  @SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
  def withBid(bid: Bid): ItemBids = {
    require(bid.item == item)

    bid.value match {
      case v if v <= 0                  => this
      case _ if higherBid.isEmpty       => ItemBids(Some(bid), bid :: bidsList)
      case v if v > higherBid.get.value => ItemBids(Some(bid), bid :: bidsList)
      case _                            => ItemBids(higherBid, bid :: bidsList)
    }
  }
}
