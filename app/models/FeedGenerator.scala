package models

import models.dao.{Item, ItemDAO}
import models.images.{Images, LargeThumb}
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import scala.xml.NodeSeq

class FeedGenerator(itemDAO: ItemDAO) {
  def allItemsFeed(baseURL: String): NodeSeq = {
    val items = itemDAO.all(false)

    def itemMapper(item: Item) = {
      <entry>
        <title>{item.name}</title>
        <link rel="alternate" type="text/html" href={"%s/items/%d".format(baseURL, item.id)}/>
        <updated>{item.createdDate.toString(ISODateTimeFormat.dateTime())}</updated>
        <published>{item.createdDate.toString(ISODateTimeFormat.dateTime())}</published>
        <id>{"%s/items/%d".format(baseURL, item.id)}</id>
        <content type="xhtml">
          <div xmlns="http://www.w3.org/1999/xhtml">
            <p>{item.description}</p>
            {item.imageKeys.map { imgKeys =>
                <p><img src={Images.generateUrl(imgKeys.split('|').head, LargeThumb)}/></p>
              }.getOrElse("") }
          </div>
        </content>
      </entry>
    }

    <feed xmlns="http://www.w3.org/2005/Atom">
      <title>Feed da Lojinha</title>
      <link href={baseURL}/>
      <link href={"%s/feed".format(baseURL)} rel="self"/>
      <updated>{new DateTime().toString(ISODateTimeFormat.dateTime())}</updated>
      <author>
        <name>jcranky</name>
      </author>
      <id>{baseURL + "/feed"}</id>

      {items.map(itemMapper)}
    </feed>
  }
}
