package com.peak.util

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.regions.{Region, Regions}
import com.amazonaws.services.sqs.AmazonSQSClient
import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.AmazonSQSClientBuilder



object SQSUtil {
  def sendSQSMessage(sqs: String, notificationMessage: String): Unit = {
    val sqsClient = new AmazonSQSClient(new DefaultAWSCredentialsProviderChain)
    sqsClient.setRegion(Region.getRegion(Regions.EU_WEST_1))
    sqsClient.sendMessage(sqs,notificationMessage)

  }


  //def sendSQSMessage(sqs: String, notificationMessage: String): Unit = ???

  def sendSQSMessage(topicArn: String, msg: String, subject :String): Unit = {
    val sqsClient = new AmazonSQSClient(new DefaultAWSCredentialsProviderChain)
    sqsClient.setRegion(Region.getRegion(Regions.EU_WEST_1))
    sqsClient.sendMessage(topicArn, msg)

  }
}
