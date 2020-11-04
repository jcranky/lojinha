package models.images

import java.awt.AlphaComposite
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class ImageThumber(image: File, imageKey: String) {
  def generateThumbs(): List[(File, String)] = ImageThumber.sizes.map(generateThumb)

  def generateThumb(thumbSize: ThumbSize): (File, String) = {
    val imageBuf = ImageIO.read(image)
    val height   = imageBuf.getHeight
    val width    = imageBuf.getWidth

    val imageName             = Images.imageName(imageKey, thumbSize)
    val (newWidth, newHeight) = ImageThumber.newSizesFor(thumbSize, width, height)

    (writeImage(newWidth, newHeight, imageBuf, thumbSize, imageName), imageName)
  }

  @SuppressWarnings(Array("org.wartremover.warts.Null", "org.wartremover.warts.NonUnitStatements"))
  private def writeImage(width: Int, height: Int, imageBuf: BufferedImage, thumbSize: ThumbSize, imageName: String) = {
    val scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    val g           = scaledImage.createGraphics
    g.setComposite(AlphaComposite.Src)
    g.drawImage(imageBuf, 0, 0, width, height, null);
    g.dispose()

    val destFile = new File(image.getParentFile, imageName)
    ImageIO.write(scaledImage, "png", destFile)

    destFile
  }
}

object ImageThumber {
  private val sizes = List(SmallThumb, MediumThumb, LargeThumb, VeryLargeThumb)

  @SuppressWarnings(Array("org.wartremover.warts.Var"))
  def newSizesFor(thumbSize: ThumbSize, originalWidth: Int, originalHeight: Int): (Int, Int) = {
    var newWidth: Double  = originalWidth
    var newHeight: Double = originalHeight

    if (newWidth > thumbSize.width) {
      newWidth = thumbSize.width
      newHeight = originalHeight * newWidth / originalWidth
    }
    if (newHeight > thumbSize.height) {
      val oldHeight = newHeight
      newHeight = thumbSize.height
      newWidth = newHeight * newWidth / oldHeight
    }

    (newWidth.toInt, newHeight.toInt)
  }
}

sealed case class ThumbSize(width: Int, height: Int, suffix: String)
object SmallThumb     extends ThumbSize(100, 100, "-small")
object MediumThumb    extends ThumbSize(200, 200, "-medium")
object LargeThumb     extends ThumbSize(300, 300, "-large")
object VeryLargeThumb extends ThumbSize(600, 600, "-verylarge")
object OriginalSize   extends ThumbSize(0, 0, "")
