package models

//can serve as inpiration when sending images to s3:
//https://github.com/jamesward/S3-Blobs-module-for-Play/blob/master/s3blobs/src/play/modules/s3blobs/S3Blob.java

import akka.actor._
import akka.routing.SmallestMailboxRouter
import java.io.File
import play.api.Play.current
import play.api.libs.concurrent.Akka
import scala.util.Random

//TODO: validate the image and return an error if its contentType isn't from an image
//TODO: redimension the images to a proper size, or make uploading really big images illegal?

object Images {
  
  val thumberRouter =
    Akka.system.actorOf(Props[ImageThumberActor].withRouter(SmallestMailboxRouter(5)), "thumber-router")
  val s3SenderRouter =
    Akka.system.actorOf(Props[S3SenderActor].withRouter(SmallestMailboxRouter(2)), "s3-sender-router")
  
  println("PATH = " + s3SenderRouter.path)
  
  /**
   * Generates a key for the image and returns it immediatelly, while sending the
   * image to be processed asynchronously with akka.
   */
  def processImage(image: File): String = {
    println("received %s to process".format(image.getName))
    
    val imageKey = new Random(image.getName.hashCode).nextString(20)
    thumberRouter ! GenThumb(image)
    
    imageKey
  }
  
  def generateUrl(imageKey: String): String = {
    // base s3 url + bucket-name + key ?
    "dummy-url - " + imageKey
  }
}

// image thumb stuff
class ImageThumberActor extends Actor {
  def receive = {
    case GenThumb(image) =>
      new ImageThumber(image).generateThumbs
      Akka.system.actorFor("akka://application/user/s3-sender-router") ! SendToS3(image)  // actually, repeat this for each generated thumb
  }
}

class ImageThumber(image: File) {
  def generateThumbs() = {
    println("thumb generation logic called - " + image.getName)
//    val newFile = new File("/tmp/" + System.currentTimeMillis + file.filename)
//    file.ref.moveTo(newFile)
  }
}

case class GenThumb(image: File)


// s3 sending stuff

class S3SenderActor extends Actor {
  def receive = {
    case SendToS3(image) =>
      new S3Sender(image).send
  }
}

class S3Sender(image: File) {
  def send() = {
    println("sending to s3..... or so you think =p " + image.getName)
  }
}

case class SendToS3(image: File)
