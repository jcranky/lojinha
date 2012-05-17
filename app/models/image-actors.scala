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
//TODO: redimension the images to a proper size, or make uploading big images illegal?
//TODO: send files to S3 keep the file bucket + key in the database?

object Images {
  
  val thumberRouter =
    Akka.system.actorOf(Props[ImageThumberActor].withRouter(SmallestMailboxRouter(5)), "thumber-router")
  
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

class ImageThumberActor extends Actor {
  def receive = {
    case GenThumb(image: File) =>
      new ImageThumber(image).generateThumbs
  }
}

class ImageThumber(image: File) {
  def generateThumbs() = {
    println("thumb generation logic called")
//    val newFile = new File("/tmp/" + System.currentTimeMillis + file.filename)
//    file.ref.moveTo(newFile)
  }
}

case class GenThumb(image: File)
