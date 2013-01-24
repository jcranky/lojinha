package models

import models.dao.ItemDAO
import scala.xml.NodeSeq

class FeedGenerator(itemDAO: ItemDAO) {
  def allItemsFeed: NodeSeq = <feed>feed!</feed>
}
