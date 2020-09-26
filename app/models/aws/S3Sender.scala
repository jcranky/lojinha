package models.aws

import java.io.File

import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.services.s3.model._
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import play.api.{Configuration, Logger}

class S3Sender(image: File, imageName: String) {

  def send() = {
    val putRequest = new PutObjectRequest(S3Sender.bucket.get, imageName, image)
    putRequest.setCannedAcl(CannedAccessControlList.PublicRead)
    
    Logger.info("sending to s3: " + imageName)
    S3Sender.s3.putObject(putRequest)
    
    if (!image.delete) Logger.info("could not delete original file %s after sending it to s3".format(imageName))
  }
}

object S3Sender {
  val config: Configuration = play.api.Play.current.configuration
  val bucket: Option[String] = config.getString("aws.s3.bucket")

  val s3: AmazonS3 = AmazonS3ClientBuilder.standard().withCredentials(
    new AWSStaticCredentialsProvider(
      new BasicAWSCredentials(config.getString("aws.accessKey").get, config.getString("aws.secretKey").get)
    )
  ).build()
}
