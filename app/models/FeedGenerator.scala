package models

import models.dao.ItemDAO
import models.images.LargeThumb
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import scala.xml.NodeSeq

class FeedGenerator(itemDAO: ItemDAO) {

  def allItemsFeed(baseURL: String): NodeSeq = {
    val items = itemDAO.all(false)

    <feed xmlns="http://www.w3.org/2005/Atom">
      <title>Feed da Lojinha</title>
      <link href={baseURL}/>
      <updated>{new DateTime().toString(ISODateTimeFormat.dateTime())}</updated>
      <author>
        <name>jcranky</name>
      </author>
      <id>{baseURL + "/feed"}</id>

      {items.map { item =>
          <entry>
            <title>{item.name}</title>
            <link href={"%s/items/%d".format(baseURL, item.id)}/>
            <id>{"%s/items/%d".format(baseURL, item.id)}</id>
            <updated>{item.createdDate.toString(ISODateTimeFormat.dateTime())}</updated>
            <summary>{item.description}</summary>
            {item.imageKeys.map { imgKeys =>
                <content src={Images.generateUrl(imgKeys.split('|').head, LargeThumb)} type="image/png"/>
              }.getOrElse() }
          </entry>
        }}
    </feed>
  }
}
