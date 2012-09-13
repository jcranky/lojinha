package models

import org.joda.time.DateTime
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification

import models.dao._

class ItemBidsSpec extends Specification with Mockito {
  implicit val item = mock[Item]
  val bid = Bid(1, "someone@jcranky.com", 10, new DateTime, item)
  val itemBids = ItemBids(Some(bid), List(bid))

  "a bid handler" should {
    "add the bid as higherBid if there is no previous bid" in {
      val emptyItemBids = ItemBids(None, Nil)

      val testBid = Bid(0, "jcranky@jcranky.com", 100, new DateTime, item)
      emptyItemBids.withBid(testBid) must_== ItemBids(Some(testBid), List(testBid))
    }

    "just store the bid in the history if there is a higher bid but don't change the higher bid" in {
      val lowBid = Bid(0, "eu@jcranky.com", 1, new DateTime, item)

      itemBids.withBid(lowBid) must_== ItemBids(Some(bid), List(lowBid, bid))
    }

    "replace the current higher bid if the new bid is higher then the current one" in {
      val superBid = Bid(0, "someonelse@jcranky.com", 999, new DateTime, item)

      itemBids.withBid(superBid) must_== ItemBids(Some(superBid), List(superBid, bid))
    }

    "completely ignore bids with negative values" in {
      val negativeBid = Bid(0, "eu@jcranky.com", -1, new DateTime, item)

      itemBids.withBid(negativeBid) must_== itemBids
    }

    "throw IllegalArgumentException if a different item is specified" in {
      val newItem = mock[Item]
      itemBids.withBid(Bid(0, "wrong@jcranky.com", 9, new DateTime, newItem)) must throwAn[IllegalArgumentException]
    }
  }
}
