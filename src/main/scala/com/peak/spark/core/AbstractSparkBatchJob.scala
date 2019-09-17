package com.peak.spark.core

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession


trait AbstractSparkBatchJob extends AbstractSparkJob {
  //var sparkCtx = null
  //var sparkSession = null
  val conf: SparkConf = new SparkConf().setAppName("AbstractSparkBatchJob")
  val sc = new SparkContext(conf)
  val spark: SparkSession = SparkSession.builder().appName("abstractSparkBatchJob").getOrCreate()

  @Override def init(): Unit = {
    ///super.init()

  }

    @Override
    def finish(): Unit = {
      sc.stop()
      spark.stop()
    }

    /**
      * Returns the JavaSparkContext
      *
      * @return { @link JavaSparkContext}
      */
    def getSparkContext: SparkContext = sc

    /**
      * Returns the SparkSession
      *
      * @return { @link JavaSparkContext}
      */
    def getSparkSession: SparkSession = spark
  }

