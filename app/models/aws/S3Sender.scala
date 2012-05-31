package models.aws

import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model._
import java.io.File

class S3Sender(image: File, imageName: String) {
  def send() = {
    val putRequest = new PutObjectRequest(S3Sender.bucket.get, imageName, image)
    putRequest.setCannedAcl(CannedAccessControlList.PublicRead)
    
    println("sending: " + imageName)
    S3Sender.s3.putObject(putRequest)
  }
}

object S3Sender {
  val config = play.api.Play.current.configuration
  val bucket = config.getString("aws.s3.bucket")
  val s3 = new AmazonS3Client(
    new BasicAWSCredentials(config.getString("aws.accessKey").get, config.getString("aws.secretKey").get))
}
