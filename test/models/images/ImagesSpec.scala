package models.images

import akka.actor.ActorSystem
import helpers.ApplicationWithDAOs
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import play.api.test.Helpers._

class ImagesSpec extends Specification with Mockito {

  "Images" should {
    "generate the correct image name" in {
      Images.imageName("asdf", LargeThumb) must_== "asdf-large.png"
    }

    "generate the correct AWS s3 URL" in new ApplicationWithDAOs(Map("aws.s3.bucket" -> "test-bucket")) {
      running(app) {
        val bucket = app.configuration.getOptional[String]("aws.s3.bucket").getOrElse("dummyBucket")
        val imageKey = "my-random-imageKey"

        new Images(mock[ActorSystem], app.configuration)
          .generateUrl(imageKey, LargeThumb) must_== "https://s3.amazonaws.com/%s/%s-large.png".format(bucket, imageKey)
      }
    }
  }
}
