package controllers

import play.api.mvc._
import models._
import models.dao._

object Feeds extends Controller {
  val feedGen = new FeedGenerator(DAOFactory.itemDAO)

  def latest = Action {
    Ok(feedGen.allItemsFeed)
  }
}
