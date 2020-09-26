package controllers

import models._
import models.dao._
import play.api.Play.current
import play.api.cache.Cache
import play.api.mvc._

import scala.xml.NodeSeq

class Feeds extends Controller {
  val feedGen = new FeedGenerator(DAOFactory.itemDAO)

  def latest = Action { implicit request =>
    FeedStatsHelper.incrementDownloadCount(request.remoteAddress)

    val feedXml = Cache.getOrElse[NodeSeq]("allItems.feed", 3600) {
      feedGen.allItemsFeed("http://" + request.host)
    }

    Ok(feedXml)
  }
}
