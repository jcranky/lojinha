package models

import akka.actor.{ Actor, ActorRef, ActorSystem, Props }
import javax.inject.{ Inject, Singleton }
import models.dao.FeedStatsDAO

class FeedStatsActor(feedStatsDAO: FeedStatsDAO) extends Actor {

  def receive = {
    case IncrementDownloadCount(origin) => feedStatsDAO.incrementDownloadCount(origin)
  }
}

@Singleton
class FeedStatsHelper @Inject() (system: ActorSystem, feedStatsDAO: FeedStatsDAO) {

  val feedStatsActor: ActorRef =
    system.actorOf(Props(new FeedStatsActor(feedStatsDAO)), "feed-stats-actor")

  def incrementDownloadCount(origin: String): Unit =
    feedStatsActor ! IncrementDownloadCount(origin)
}

final case class IncrementDownloadCount(origin: String)
