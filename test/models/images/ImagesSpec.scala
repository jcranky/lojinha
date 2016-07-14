package models.images

import akka.actor.ActorSystem
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import play.api.test._
import play.api.test.Helpers._

class ImagesSpec extends Specification with Mockito {

  "the Images object" should {
    "generate the correct image name" in {
      new Images(mock[ActorSystem]).imageName("asdf", LargeThumb) must_== "asdf-large.png"
    }

    "generate the correct AWS s3 URL" in {
      running(FakeApplication(additionalConfiguration = Map("aws.s3.bucket" -> "test-bucket"))) {
        val bucket = play.api.Play.current.configuration.getString("aws.s3.bucket").getOrElse("dummyBucket")
        val imageKey = "my-random-imageKey"

        new Images(mock[ActorSystem]).generateUrl(imageKey, LargeThumb) must_== "https://s3.amazonaws.com/%s/%s-large.png".format(bucket, imageKey)
      }
    }
  }
}
