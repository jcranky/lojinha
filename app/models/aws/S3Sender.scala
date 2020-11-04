package models.aws

import java.io.File

import com.amazonaws.auth.{ AWSStaticCredentialsProvider, BasicAWSCredentials }
import com.amazonaws.services.s3.model._
import com.amazonaws.services.s3.{ AmazonS3, AmazonS3ClientBuilder }
import play.api.{ Configuration, Logger, Logging }

class S3Sender(configuration: Configuration)(image: File, imageName: String) extends Logging {
  val bucket: Option[String] = configuration.getOptional[String]("aws.s3.bucket")

  val s3: AmazonS3 = AmazonS3ClientBuilder
    .standard()
    .withCredentials(
      new AWSStaticCredentialsProvider(
        new BasicAWSCredentials(
          // fixme: replace get with getOptional
          configuration.get[String]("aws.accessKey"),
          configuration.get[String]("aws.secretKey")
        )
      )
    )
    .build()

  @SuppressWarnings(Array("org.wartremover.warts.OptionPartial", "org.wartremover.warts.NonUnitStatements"))
  def send(): Unit = {
    val putRequest = new PutObjectRequest(bucket.get, imageName, image)
    putRequest.setCannedAcl(CannedAccessControlList.PublicRead)

    logger.info("sending to s3: " + imageName)
    s3.putObject(putRequest)

    if (!image.delete) logger.info("could not delete original file %s after sending it to s3".format(imageName))
  }
}
