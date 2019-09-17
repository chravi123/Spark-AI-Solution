package com.peak.util

import java.io.{File, InputStream, UnsupportedEncodingException}
import java.net.URLDecoder
import java.util.regex.Pattern

import com.amazonaws.AmazonServiceException
import com.amazonaws.auth.{AWSCredentialsProvider, DefaultAWSCredentialsProviderChain}
import com.amazonaws.services.s3.{AmazonS3, AmazonS3Client}
import com.amazonaws.services.s3.model.{ObjectMetadata, PutObjectRequest, PutObjectResult}
import com.amazonaws.util.StringUtils
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.io.IOUtils
import org.slf4j.{Logger, LoggerFactory}


object S3Util {
   var amazonS3Client:AmazonS3Client = amazonS3Client
   val UTF_8 = "UTF-8"
   val S3_PREFIX = "s3://"
   val pattern:Pattern = Pattern.compile("(s3://)(.*?)/")
   val logger: Logger = LoggerFactory.getLogger(classOf[S3Util])
   val mapper = new ObjectMapper
   var s3Util:S3Util = s3Util
   val MEGABYTE:Long = 1024L * 1024L

  def getInstance: S3Util = s3Util

  try s3Util = new S3Util

}

//noinspection ScalaDocMissingParameterDescription
class S3Util {
   val credentialsProvider:AWSCredentialsProvider = credentialsProvider

  def getS3Client: AmazonS3 = {
    if (null == S3Util.amazonS3Client) refresh()
    S3Util.amazonS3Client
  }

  /**
    * Made it public to refresh on secret key expiration,
    */
  def refresh(): Unit = {
    S3Util.amazonS3Client = new AmazonS3Client(new DefaultAWSCredentialsProviderChain)
  }

  /**
    * Decode S3 urls to utf-8
    *
    * @param input
    * @return
    */
  def decode(input: String): String = try
    URLDecoder.decode(input, S3Util.UTF_8)
  catch {
    case _: UnsupportedEncodingException =>
      input
  }

  /**
    * Get URI
    *
    * @param bucket
    * @param key
    * @return
    */
  def uri(bucket: String, key: String): String = String.format("s3://%s/%s", bucket, key)

  /**
    * Get bucket name from s3 url (s3://blahblah/blah/abc.txt
    *
    * @param url
    * @return
    */
  def getBucket(url: String): String = url.split("[/]")(2)

  /**
    * Get key Example: "s3://bucket/key/abc.txt" to "key/abc.txt"
    *
    * @param url
    * @return
    */
  def getKey(url: String): String = {
    val bucket = this.getBucket(url)
    url.substring(url.indexOf(bucket) + bucket.length + 1)
  }

  /**
    * Uploads a file on the local filesystem to S3
    *
    * @param bucket       S3 bucket to upload the file to
    * @param key          Key where the file will be uploaded
    * @param absolutePath Local file to upload to S3
    * @return the { @link PutObjectResult}
    */
  @throws[AmazonServiceException]
  def upload(bucket: String, key: String, absolutePath: String): PutObjectResult = {
    val file = new File(absolutePath)
    if (StringUtils.isNullOrEmpty(key))
      file.getName
    else if (key.endsWith("/"))
       key + file.getName
    S3Util.logger.info(String.format("Uploading %s to %s/%s", absolutePath, bucket, key))
    this.getS3Client.putObject(bucket, key, file)
  }

  /**
    * Upload content to s3 as an input stream. Useful if you have some generate content
    * and don't want to write it to a local file first just to upload to s3.
    *
    * @param bucket        - the s3 bucket to upload to
    * @param key           - the key (path) of the content to upload
    * @param inputStream   - the inputstream representing the content to upload
    * @param contentLength - the length of the content in the input stream.
    *                      For performance reasons, clients MUST set the content length in the metadata object,
    *                      else the entire input stream will be buffered into memory, with potentially
    *                      dangerous performance implications
    *
    * @return the { @link PutObjectResult} if successful, null if the request params are invalid
    * @throws AmazonServiceException if the request is unsuccessful
    *                                mockS3Util.upload("dp-unit-test", "key", IOUtils.toInputStream(testContent), testContent.length());
    */
  @throws[AmazonServiceException]
  def upload(bucket: String, key: String, inputStream: InputStream, contentLength: Long): PutObjectResult = {
    if (StringUtils.isNullOrEmpty(bucket) || StringUtils.isNullOrEmpty(key) || inputStream == null || contentLength <= 0) {
      S3Util.logger.warn("Unable to send upload content to s3, invalid input parameters")
      return null
    }
    val metadata = new ObjectMetadata
    metadata.setContentLength(contentLength)
    upload(bucket, key, inputStream, metadata)
  }

  /**
    * Upload content to s3 as an input stream. Useful if you have some generate content
    * and don't want to write it to a local file first just to upload to s3.
    *
    * @param bucket      - the s3 bucket to upload to
    * @param key         - the key (path) of the content to upload
    * @param inputStream - the inputstream representing the content to upload
    * @param metadata    - metadata related to the request. Note for performance reasons, clients
    *                    MUST set the content length in the metadata object, else the entire input stream will
    *                    be buffered into memory, with potentially dangerous performance implications
    *
    * @return the { @link PutObjectResult} if successful, null if the request is invalid
    * @throws AmazonServiceException if the request is unsuccessful
    */
  @throws[AmazonServiceException]
  def upload(bucket: String, key: String, inputStream: InputStream, metadata: ObjectMetadata): PutObjectResult = {
    if (StringUtils.isNullOrEmpty(bucket) || StringUtils.isNullOrEmpty(key) || inputStream == null || metadata == null) {
      S3Util.logger.warn("Unable to send upload content to s3, invalid input parameters")
      return null
    }
    val request = new PutObjectRequest(bucket, key, inputStream, metadata)
    putObject(request)
  }

  /**
    * Makes a put request to the s3 api with the provided request object.
    *
    * @param request
    * @return the { @link PutObjectResult} if successful, null if the request is invalid
    * @throws AmazonServiceException if the request is unsuccessful
    */
  @throws[AmazonServiceException]
  def putObject(request: PutObjectRequest): PutObjectResult = {
    if (request == null) {
      S3Util.logger.warn("Unable to send upload content to s3, put object request was null")
      return null
    }
    getS3Client.putObject(request)
  }
}
