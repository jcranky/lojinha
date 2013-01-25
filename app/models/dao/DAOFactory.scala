package models.dao

import models.dao.anorm._

object DAOFactory {
  def categoryDAO: CategoryDAO =  AnormCategoryDAO

  def itemDAO: ItemDAO = AnormItemDAO

  def bidDAO: BidDAO = AnormBidDAO

  def userDAO: UserDAO = AnormUserDAO

  def feedStatsDAO: FeedStatsDAO = AnormFeedStatsDAO
}
