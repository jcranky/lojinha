package models.dao.anorm

import anorm._
import anorm.SqlParser._
import models.dao.{FeedStats, FeedStatsDAO}
import play.api.db.DB
import play.api.Play.current

object AnormFeedStatsDAO extends FeedStatsDAO {
  val feedStats = {
    str("origin") ~ int("download_count") map {
      case origin ~ downloadCount => FeedStats(origin, downloadCount)
    }
  }

  def incrementDownloadCount(origin: String): Unit = DB.withConnection { implicit c =>
    val feedStatOpt = SQL("SELECT * FROM feed_stats WHERE origin = {origin}").on('origin -> origin).as(feedStats singleOpt)
    val feedStat = feedStatOpt.getOrElse {
      SQL("INSERT INTO feed_stats(origin, download_count) VALUES({origin}, 0)").on('origin -> origin).executeUpdate()
      FeedStats(origin, 0)
    }

    SQL("UPDATE feed_stats SET download_count = {count} WHERE origin = {origin}").on(
      'count -> (feedStat.downloadCount + 1), 'origin -> origin).executeUpdate()
  }
}
