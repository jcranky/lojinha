package models

import akka.actor.{Actor, Props}
import models.dao.DAOFactory
import play.api.Play.current
import play.api.libs.concurrent.Akka

class FeedStatsActor extends Actor {
  val feedStatsDAO = DAOFactory.feedStatsDAO

  def receive = {
    case IncrementDownloadCount(origin) => feedStatsDAO.incrementDownloadCount(origin)
  }
}

object FeedStatsHelper {
  val feedStatsActor = Akka.system.actorOf(Props[FeedStatsActor], "feed-stats-actor")

  def incrementDownloadCount(origin: String) = feedStatsActor ! IncrementDownloadCount(origin)
}

case class IncrementDownloadCount(origin: String)
