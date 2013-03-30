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
    case EmailMessage(name, url, to) =>
      val mail = use[MailerPlugin].email
      mail.setSubject("better bid received")
      mail.addRecipient(to)
      mail.addFrom("Lojinha JCranky <noreply@jcranky.com>")
      mail.sendHtml(views.html.email.bidTopped.render(name, url).body)
  }
}

case class EmailMessage(name: String, url: String, to: String)
