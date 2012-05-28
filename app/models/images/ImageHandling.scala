package models.images

import java.io.File

class ImageThumber(image: File, imageKey: String) {
  def generateThumbs() = {
    println("thumb generation logic called - " + image.getName)
//    val newFile = new File("/tmp/" + System.currentTimeMillis + file.filename)
//    file.ref.moveTo(newFile)
  }
}

case class ThumbSize(witdth: Int, height: Int, suffix: String)

object ImageThumber {
  val sizes = List(ThumbSize(10, 10, "small"), ThumbSize(100, 100, "medium"), ThumbSize(200, 200, "large"))
  //?
}
