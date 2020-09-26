package models

import akka.actor._
import play.api.Play.current
import play.api.libs.concurrent.Akka
import play.api.libs.mailer._

object EMailActor {
  val actor: ActorRef = Akka.system.actorOf(Props(
    new EmailActor(new SMTPMailer(new SMTPConfiguration("localhost", 25)))
  ))
}

class EmailActor(mailerClient: MailerClient) extends Actor {
  def receive = {
    case m: BidToppedMessage => sendEmail(m, views.html.email.bidTopped.render(m.itemName, m.itemUrl).body)
    case m: BidReceivedMessage => sendEmail(m, views.html.email.bidReceived.render(m.itemName, m.itemUrl).body)
  }
  
  def sendEmail(m: EmailMessage, body: String) {
    val email = Email(
      m.subject,
      "Lojinha JCranky <noreply@jcranky.com>",
      Seq(m.to),
      bodyText = Some(body)
    )
    mailerClient.send(email)
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

case class BidReceivedMessage(itemName: String, itemUrl: String, to: String) extends EmailMessage {
  val subject = "your bid has been received"
}
