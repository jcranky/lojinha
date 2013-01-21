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
    case EmailMessage(msg, to) =>
      val mail = use[MailerPlugin].email
      mail.setSubject("better bid received")
      mail.addRecipient(to)
      mail.addFrom("Lojinha JCranky <noreply@jcranky.com>")
      mail.send(msg)
  }
}

case class EmailMessage(msg: String, to: String)
