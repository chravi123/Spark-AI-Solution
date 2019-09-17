package com.peak.util

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value


object SQSNotification {
  @throws[JsonProcessingException]
  def main(args: Array[String]): Unit = {
    val no = new SQSNotification
    no.setErrorMessage("tetingjavaclient")
    //Object mapper instance
    val mapper = new ObjectMapper
    //Convert POJO to JSON
    val json = mapper.writeValueAsString(no)
    System.out.println(json)
  }
}

class SQSNotification {
          var errorMessage: String = errorMessage
          var feedRunId:String = feedRunId
          var feedId:String = feedId
          var runTime:String = runTime
          var totalRowCount:Long = totalRowCount
          var failRowCount:Long = failRowCount
          var successRowCount:Long = successRowCount
          var Tenant:String = Tenant
          var headers:String = headers
  @Value("${data.output.location:}")
  var dataOutputLoc: String = dataOutputLoc
  var status: String = status


  def getErrorMessage: String = errorMessage

  def setErrorMessage(errorMessage: String): Unit = {
    this.errorMessage = errorMessage
  }

  def getStatus: String = status

  def setStatus(status: String): Unit = {
    this.status = status
  }

  def getFeedRunId: String = feedRunId

  def setFeedRunId(feedRunId: String): Unit = {
    this.feedRunId = feedRunId
  }

  def getFeedId: String = feedId

  def setFeedId(feedId: String): Unit = {
    this.feedId = feedId
  }

  def getrunTime: String = runTime

  def setrunTime(runTime: String): Unit = {
    this.runTime = runTime
  }

  def getfailRowCount: Long = totalRowCount

  def setfailRowCount(failRowCount: Long): Unit = {
    this.failRowCount = failRowCount
  }

  def getsuccessRowCount: Long = successRowCount

  def setsuccessRowCount(successRowCount: Long): Unit = {
    this.successRowCount = successRowCount
  }

  def gettotalRowCount: Long = totalRowCount

  def settotalRowCount(totalrowCount: Long): Unit = {
    this.totalRowCount = totalrowCount
  }

  def getTenant:String = Tenant

    def setTenant(Tenant:String): Unit ={
      this.Tenant = Tenant
    }

    def getheaders:String = headers

    def setheaders(headers:String): Unit ={
      this.headers = headers
    }

    def getdataOutputLoc:String = dataOutputLoc

    def setdataOutputLoc(dataOutputLoc:String): Unit ={
      this.dataOutputLoc = dataOutputLoc
    }





}
