package models.images

import org.specs2.mutable.Specification

class ImageThumberSpec extends Specification {
  "the ImageThumber" should {
    "return the same width and height for sizes smaller than the thumb size" in {
      ImageThumber.newSizesFor(SmallThumb, 50, 60) must_== ((50, 60))
      ImageThumber.newSizesFor(SmallThumb, 70, 60) must_== ((70, 60))
    }
    
    "return correct new sizes when the real width is twice the thumb width" in {
      ImageThumber.newSizesFor(SmallThumb, 200, 100) must_== ((100, 50))
    }
    
    "return correct new sizes when both dimensions are bigger than the thumb's and width > height" in {
      ImageThumber.newSizesFor(SmallThumb, 500, 375) must_== ((100, 75))
    }
    
    "return correct new sizes when both dimensions are bigger than the thumb's and height > width" in {
      ImageThumber.newSizesFor(SmallThumb, 375, 500) must_== ((75, 100))
    }
  }
}
