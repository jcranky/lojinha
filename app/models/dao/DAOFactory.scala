package models.dao

object DAOFactory {
  def categoryDAO: CategoryDAO =  AnormCategoryDAO

  def itemDAO: ItemDAO = AnormItemDAO

  def bidDAO: BidDAO = AnormBidDAO

  def userDAO: UserDAO = AnormUserDAO
}
