package models.aws

import java.io.File

class S3Sender(image: File, imageKey: String) {
  def send() = {
    println("sending to s3..... or so you think =p " + image.getName)
  }
}
