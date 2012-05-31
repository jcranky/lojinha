package models

import akka.actor._
import akka.routing.SmallestMailboxRouter
import java.io.File
import models.aws.S3Sender
import models.images.ImageThumber
import play.api.Play.current
import play.api.libs.concurrent.Akka
import scala.util.Random

object Images extends Images(Akka.system)

class Images(system: ActorSystem) {
  val thumberRouter =
    system.actorOf(Props[ImageThumberActor].withRouter(SmallestMailboxRouter(5)), "thumber-router")
  val s3SenderRouter =
    system.actorOf(Props[S3SenderActor].withRouter(SmallestMailboxRouter(2)), "s3-sender-router")
  
  /**
   * Generates a key for the image and returns it immediatelly, while sending the
   * image to be processed asynchronously with akka.
   */
  def processImage(image: File): String = {
    val validChars = "abcdefghijklmnopqwxyv_"
    val imageKey = (1 to 20).foldLeft("")((t, a) => t + validChars(Random.nextInt(validChars.length)))
    thumberRouter ! GenThumb(image, imageKey)
    
    imageKey
  }
  
  def generateUrl(imageKey: String): String = {
    //TODO: base s3 url + bucket-name + key ?
    "dummy-url - " + imageKey
  }
}

class ImageThumberActor extends Actor {
  def receive = {
    case GenThumb(image, imageKey) =>
      val images = new ImageThumber(image, imageKey).generateThumbs
      val s3SenderRouter = context.system.actorFor("akka://application/user/s3-sender-router")
      
      s3SenderRouter ! SendToS3(image, imageKey + ".png")
      images foreach { imageTuple =>
        val (imageFile, imageName) = imageTuple
        s3SenderRouter ! SendToS3(imageFile, imageName) 
      }
  }
}

class S3SenderActor extends Actor {
  def receive = {
    case SendToS3(image, imageKey) =>
      new S3Sender(image, imageKey).send
  }
}

case class GenThumb(image: File, imageKey: String)
case class SendToS3(image: File, imageName: String)
