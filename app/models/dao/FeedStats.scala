package models.dao

import com.google.inject.ImplementedBy
import models.dao.anorm.AnormFeedStatsDAO

final case class FeedStats(origin: String, downloadCount: Int)

@ImplementedBy(classOf[AnormFeedStatsDAO])
trait FeedStatsDAO {
  def incrementDownloadCount(origin: String): Unit;
}
