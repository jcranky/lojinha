package models

import akka.actor._
import com.typesafe.plugin._
import play.api.Play.current
import play.api.libs.concurrent.Akka

object EMail {
  val actor = Akka.system.actorOf(Props[EmailActor])
}

class EmailActor extends Actor {
  def receive = {
    case m: BidToppedMessage => sendEmail(m, views.html.email.bidTopped.render(m.itemName, m.itemUrl).body)
    case m: BidAcceptedMessage => sendEmail(m, views.html.email.bidAccepted.render(m.itemName, m.itemUrl).body)
  }
  
  def sendEmail(m: EmailMessage, body: String) {
    val mail = use[MailerPlugin].email
    mail.setSubject(m.subject)
    mail.addRecipient(m.to)
    mail.addFrom("Lojinha JCranky <noreply@jcranky.com>")
    mail.sendHtml(body)
  }
}

sealed trait EmailMessage {
  val itemName: String
  val itemUrl: String
  val to: String
  val subject: String
}

case class BidToppedMessage(itemName: String, itemUrl: String, to: String) extends EmailMessage {
  val subject = "better bid received"
}

case class BidAcceptedMessage(itemName: String, itemUrl: String, to: String) extends EmailMessage {
  val subject = "your bid has been accepted"
}
