package models

import models.dao.{ Item, ItemDAO }
import models.images.{ Images, LargeThumb }
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat

import scala.xml.NodeSeq

class FeedGenerator(itemDAO: ItemDAO, images: Images) {

  @SuppressWarnings(Array("org.wartremover.warts.JavaSerializable", "org.wartremover.warts.Serializable"))
  def allItemsFeed(baseURL: String): NodeSeq = {
    val items = itemDAO.all(false)

    def itemImages(item: Item): NodeSeq =
      item.imageKeys
        .map { imgKeys =>
          <p><img src={images.generateUrl(imgKeys.split('|').head, LargeThumb)}/></p>
        }
        .getOrElse(NodeSeq.Empty)

    def itemMapper(item: Item): NodeSeq =
      <entry>
        <title>{item.name}</title>
        <link rel="alternate" type="text/html" href={"%s/items/%d".format(baseURL, item.id)}/>
        <updated>{item.createdDate.toString(ISODateTimeFormat.dateTime())}</updated>
        <published>{item.createdDate.toString(ISODateTimeFormat.dateTime())}</published>
        <id>{"%s/items/%d".format(baseURL, item.id)}</id>
        <content type="xhtml">
          <div xmlns="http://www.w3.org/1999/xhtml">
            <p>{item.description}</p>
            {itemImages(item)}
          </div>
        </content>
      </entry>

    val feed: NodeSeq = <feed xmlns="http://www.w3.org/2005/Atom">
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

    feed
  }
}
