package com.peak.spark.core

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.support.ClassPathXmlApplicationContext
import com.peak.spark.jobs.SparkConfigKeys


/**
  *
  * Start point of spark application
  *
  * Example: spark-submit --conf
  * "spark.executor.extraJavaOptions=-Ddata.input.location=s3://test/
  * -Dcompany=peak" --conf
  * "spark.driver.extraJavaOptions=-Ddata.input.location=s3://test/
  * -Dcompany=peak" --class com.peak.core.spark.SparkApp
  * /tmp/data-stream-1.0.0-SNAPSHOT.jar oneid-log-preprocessor-dev
  *
  */
@ComponentScan
object SparkApp {
  private val logger:Logger = LoggerFactory.getLogger(this.getClass)


   def launch(args: Array[String]): Unit = {
    if (args.length == 0 )
      throw new RuntimeException("Job id is mandatory as command line argument. [arg1=jobid, example tenant-preprocessor-dev]")
    else {
      val jobId = args(1)
      // setting system props
      java.lang.System.setProperty(SparkConfigKeys.DP_SPARK_JOB_ID.toString, jobId)
      val ctx2 = new ClassPathXmlApplicationContext("spring/*.xml")
      ctx2.start()
    }
  }

  /**
    * Main execution for Spark Jobs. This class is responsible for initializing the
    * Spring Context and kicking off the spark job
    *
    * How to run spark-submit com.peak.core.spark.SparkApp <job-id>
    * <environment> Example: spark-submit com.peak.core.spark.SparkApp
    * cache-fuse dev
    *
    * @param args
    */
  //noinspection ScalaDocMissingParameterDescription

  def main(args: Array[String]): Unit = {
    logger.info("Starting spark app main at {} ", PeakSystem.getName)
    launch(args)
    logger.info("Finished spark app main at {} ", PeakSystem.getName)
  }
}
