package controllers

import javax.inject.Inject
import models._
import models.dao._
import models.images.Images
import play.api.cache.SyncCacheApi
import play.api.mvc._

import scala.concurrent.duration.FiniteDuration
import scala.xml.NodeSeq

class Feeds @Inject() (images: Images, itemDAO: ItemDAO, feedStatsHelper: FeedStatsHelper,
                       cache: SyncCacheApi, val controllerComponents: ControllerComponents) extends BaseController {

  // fixme: inject this instead of creating it here
  val feedGen = new FeedGenerator(itemDAO, images)

  def latest = Action { implicit request =>
    feedStatsHelper.incrementDownloadCount(request.remoteAddress)

    val feedXml = cache.getOrElseUpdate[NodeSeq]("allItems.feed", FiniteDuration(3600, "seconds")) {
      feedGen.allItemsFeed("http://" + request.host)
    }

    Ok(feedXml)
  }
}
