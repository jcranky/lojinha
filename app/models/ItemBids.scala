package models

import models.dao._

case class ItemBids(higherBid: Option[Bid], bidsList: List[Bid] = Nil)(implicit val item: Item) {
  def this(bidsList: List[Bid], item: Item) =
    this(if(bidsList.size > 0) Some(bidsList.max) else None, bidsList)(item)
  
  def withBid(bid: Bid) = {
    require (bid.item == item)
    
    bid.value match {
      case v if v <= 0 => this
      case v if !higherBid.isDefined => ItemBids(Some(bid), bid :: bidsList)
      case v if v > higherBid.get.value => ItemBids(Some(bid), bid :: bidsList)
      case _ => ItemBids(higherBid, bid :: bidsList)
    }    
  }
}
