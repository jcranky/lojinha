package models.dao

case class FeedStats(origin: String, downloadCount: Int)

trait FeedStatsDAO {
  def incrementDownloadCount(origin: String): Unit;
}
