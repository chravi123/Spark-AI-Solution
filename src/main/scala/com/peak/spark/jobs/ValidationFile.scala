package com.peak.spark.jobs


import java.io.{FileNotFoundException, PrintWriter, Serializable}
import java.util

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.peak.spark.core.AbstractSparkBatchJob
import com.peak.util.{S3Util, SQSNotification, SQSUtil}
import com.peak.util.SQSUtil.sendSQSMessage
import org.apache.commons.io.IOUtils
import org.apache.spark.sql._
import org.apache.spark.sql.functions.{col, _}
import org.apache.spark.sql.types.{DataTypes, StructType}
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Component

import scala.collection.Seq
import scala.util.matching.Regex

@Component("ValidationFile")
object ValidationFile {

}

//noinspection ScalaUnusedSymbol
//@Component("ValidationFile")
class ValidationFile extends AbstractSparkBatchJob with Serializable {
   val regex: Regex = "[^a-zA-Z0-9]".r
   var cnt:Long = 0L
   var successRowCount: Long = successRowCount
   var  failRowCount: Long =  failRowCount

  @Value("${data.input.location:}")
  val dataInputLoc:String = dataInputLoc
  @Value("${data.output.location:}")
  val dataOutputLoc:String = dataOutputLoc
  @Value("${data.output.error.location:}")
  val dataOutputerrorLoc:String = dataOutputerrorLoc

  @Value("${sns.alert.address:}")
  val snsAlert:String = snsAlert

  @Value("${feedId:}")
  val feedId:String = feedId
  @Value("${feedRunId:}")
  val feedRunId:String = feedRunId

  @Value("${SQS:}")
  val sqs:String = sqs


  case class IsEmptyRow(id: Int, description: String)

  override def exec(): Unit = {
    var spark = null
    try {
      val spark = this.getSparkSession
      val data = spark.read.format("csv").option("header", "true")
        .option("inferSchema", "true")
        .option("delimiter", ",")
        .option("quote", "\"")
        .option("encoding", "UTF-8")
        .option("escape", "\"")
        .option("dateFormat", "yyyyMMdd")
        .option("TimeFormat", "HH:mm:ss")
        .load(dataInputLoc)

      println(data.count())
       var  newData = data
      for (col <- data.columns) {
       newData= newData.withColumnRenamed(col, col.replaceAll("regex", ""))
      }


      val df2 = newData.filter(data.columns.map(x => col(x) === "").reduce(_ || _))


      import spark.implicits._

      //implicit def optionalInt: org.apache.spark.sql.Encoder[Option[Int]] = org.apache.spark.sql.catalyst.encoders.ExpressionEncoder()




      val EmptyDf = df2.map {
        row => row.getInt(row.fieldIndex("id")) -> row
          .toSeq
          .zip(df2.columns)
          .collect {
          case (value: String, column)
            if value.isEmpty => column
        }

      }.map{

        case (id, List(column))  => IsEmptyRow(id, s" $column column with empty values").copy()
        case (id, columns) => IsEmptyRow(id, s"${columns.mkString(", ")} columns with empty values")

      }



      val df3 = df2.join(EmptyDf).drop("id")




      val df4 = newData.filter(data.columns.map(x => col(x) =!= "").reduce(_ && _))

    } catch {
      case e: Exception =>
        e.printStackTrace()
        val jobid = sc.applicationId
        val notification:SQSNotification = new SQSNotification
        notification.setFeedId(feedId)
        notification.setFeedRunId(feedRunId)
        notification.setStatus("fail")
        //notification.setApplicationId(jobid)
        notification.setErrorMessage(e.getMessage)
        val mapper = new ObjectMapper
        val notificationMessage = null
        try {
          val notificationMessage = mapper.writeValueAsString(notification)
        }catch {
          case e1: JsonProcessingException =>
            // TODO Auto-generated catch block
            e1.printStackTrace()
        }
        val subject = "complete Spark"
        SQSUtil.sendSQSMessage(sqs, notificationMessage, subject)
        throw new RuntimeException("Spark Job Failed" + e.getMessage)
    }
  }



  def sqsmsg(spark: SparkSession): Unit = {
    val notification:SQSNotification = new SQSNotification
    val jobid = spark.sparkContext.applicationId
     notification.setFeedId(feedId)
     notification.setFeedRunId(feedRunId)
     notification.setStatus("success")
     notification.settotalRowCount(cnt)
     notification.setsuccessRowCount(successRowCount)
     notification.setfailRowCount(failRowCount)
    val mapper = new ObjectMapper
    val notificationMessage = null
    try
     mapper.writeValueAsString(notification)
    catch {
      case e1: JsonProcessingException =>
        e1.printStackTrace()
    }
    val subject = "complete Spark"
    sendSQSMessage(sqs, notificationMessage)
  }

  def writeData(df3: DataFrame, location: String): Unit = {
    if (location.startsWith("s3://"))

    df3.write.format("csv").option("header", "true")
      .option("dateFormat", "yyyyMMdd").option("TimeFormat", "HH:mm:ss").option("inferSchema", "true").mode(SaveMode.Overwrite)
      .save(dataOutputerrorLoc)
    df3.show(false)
    df3.printSchema()
    //val df4 = df2.join(isEmptyDf)
    //df4.show(false)
    println(df3.count())

    println(df3.schema.fieldNames.reduce(_ + "," + _))


  }


  def writeData1(df4: DataFrame, location: String): Unit = {
    if (location.startsWith("s3://"))
      df4.write.format("csv").option("header", "true")
        .option("dateFormat", "yyyyMMdd").option("TimeFormat", "HH:mm:ss").option("inferSchema", "true")
        .save(dataOutputLoc)
    df4.show()
    df4.printSchema()
    println(df4.count())
    println(df4.schema.fieldNames.reduce(_ + "," + _))

  }
}
