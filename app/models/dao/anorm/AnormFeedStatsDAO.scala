package models.dao.anorm

import anorm.SqlParser._
import anorm._
import javax.inject.{Inject, Singleton}
import models.dao.{FeedStats, FeedStatsDAO}
import play.api.db.Database

@Singleton
class AnormFeedStatsDAO @Inject() (db: Database) extends FeedStatsDAO {

  val feedStats: RowParser[FeedStats] = {
    str("origin") ~ int("download_count") map {
      case origin ~ downloadCount => FeedStats(origin, downloadCount)
    }
  }

  def incrementDownloadCount(origin: String): Unit = db.withConnection { implicit c =>
    val feedStatOpt = SQL("SELECT * FROM feed_stats WHERE origin = {origin}").on('origin -> origin).as(feedStats singleOpt)
    val feedStat = feedStatOpt.getOrElse {
      SQL("INSERT INTO feed_stats(origin, download_count) VALUES({origin}, 0)").on('origin -> origin).executeUpdate()
      FeedStats(origin, 0)
    }

    SQL("UPDATE feed_stats SET download_count = {count} WHERE origin = {origin}").on(
      'count -> (feedStat.downloadCount + 1), 'origin -> origin).executeUpdate()
  }
}
