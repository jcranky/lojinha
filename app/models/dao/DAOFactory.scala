package models.dao

import models.dao.anorm._

// fixme: change all DAOs to use Dependency Injection instead of this factory directly
object DAOFactory {
  def categoryDAO: CategoryDAO =  AnormCategoryDAO

  def itemDAO: ItemDAO = AnormItemDAO

  def bidDAO: BidDAO = AnormBidDAO

  def userDAO: UserDAO = AnormUserDAO

  def feedStatsDAO: FeedStatsDAO = AnormFeedStatsDAO
}
