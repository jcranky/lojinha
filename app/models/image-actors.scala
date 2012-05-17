package models

//can serve as inpiration:
//https://github.com/jamesward/S3-Blobs-module-for-Play/blob/master/s3blobs/src/play/modules/s3blobs/S3Blob.java

import java.io.File

//TODO: validate the image and return an error if its contentType isn't from an image
//TODO: redimension the image to a proper size, or make uploading big images illegal
//TODO: generate thumbnails of the images?        
//TODO: send files to S3 keep the file bucket + key in the database?

object Images {
  
  /**
   * Generates a key for the image and returns it immediatelly, while sending the
   * image to be processed asynchronously with akka.
   */
  def processImage(image: File): String = {
    println("received %s to process".format(image.getName))
    
    //TODO: replace this with a not-dumb way of getting unique filenames
    
//    val newFile = new File("/tmp/" + System.currentTimeMillis + file.filename)
//    file.ref.moveTo(newFile)
    
    image.getName
  }
  
  def generateUrl(imageKey: String): String = {
    "dummy-url - " + imageKey
  }
}

