package com.peak.spark.core

import java.io.IOException
import java.util.Properties

import com.peak.spark.jobs.SparkConfigKeys
import jdk.internal.org.objectweb.asm.tree.analysis.Value
import org.apache.hadoop.mapreduce.Job
import org.apache.livy.shaded.jackson.annotation.JsonFormat.Value
import org.apache.log4j.PropertyConfigurator
import org.apache.spark.SparkConf
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired





object AbstractSparkJob {
  val logger: Logger = LoggerFactory.getLogger(classOf[AbstractSparkJob])}


   //noinspection ScalaDeprecation,ScalaDocMissingParameterDescription
    abstract class AbstractSparkJob  {


    @Autowired
    val sparkConf: SparkConf = sparkConf
    @Autowired
    val config: Properties = config
    var jobId: String = jobId
    var job: Job = job
    var partition: Partition = partition

    /**
      * Initializes all spark job properties before execution. This class may
      * also be used to initialize the source.
      */
    private def init(): Unit = {
      this.loadProps()
      try {
        this.job = Job.getInstance
      } catch {
        case _: IOException =>
          AbstractSparkJob.logger.error("Unable to get Job instance.")
          throw new RuntimeException("Unable to get Job instance.")
      }
      //setting partitioner bean
      if (null == partition) {
        partition = Spring.bridge(getJobId).get(SparkConfigKeys.stringVal(getConfig), classOf[Partition])
      }
    }

    /**
      * Defines the Spark Job execution flow
      */
    def exec(): Unit

    /**
      * Executes all post-processing tasks
      */
    def finish(): Unit

    /**
      * Runs the spark job
      */
     def run(): Unit = {
      AbstractSparkJob.logger.info("Starting spark job {} at {} ")
      AbstractSparkJob.logger.info(jobId)
      AbstractSparkJob.logger.info(PeakSystem.getName)
      init()
      exec()
      finish()
      AbstractSparkJob.logger.info("Spark job {} finished at {} ")
    }

    /**
      * Returns the SparkConf for this spark job
      *
      * @return { @link SparkConf}
      */
    def getSparkConf: SparkConf = sparkConf

    /**
      * Returns the properties for this spark job
      *
      * @return { @link Properties}
      */
    def getConfig: Properties = config

    /**
      * Loads all Spark properties from the config into the SparkConf for this
      * spark job
      */
    private def loadProps(): Unit = {
      import scala.collection.JavaConversions._
      for (prop <- config.stringPropertyNames) {
        if (prop.startsWith("spark.")) {
          sparkConf.set(prop, config.getProperty(prop))
          AbstractSparkJob.logger.info("Setting sparkConf {}={}")
          AbstractSparkJob.logger.info(prop)
          AbstractSparkJob.logger.info(config.getProperty(prop))

        }
      }
    }

    /**
      * @param jobId
      * @return jobId
      *//**
      * changing log conf file
      */
    //    private void setCustomLogger(){
    //        PropertyConfigurator.configure(getClass().getResourceAsStream(getLogConf()));
    //    }
    def setJobId(jobId: String): AbstractSparkJob = {
      this.jobId = jobId
      this
    }

    /**
      * Get Job Id
      *
      * @return
      */
    def getJobId: String = jobId

    /**
      * Returns Job instance
      *
      * @return
      */
    def getJob: Job = this.job

    /**
      * Returns the partitioner
      *
      * @return
      */
    def getPartition: Partition = partition

    /**
      * Returns temporary directory specified in the config
      *
      * @return
      */
    def getTempDir: String = SparkConfigKeys.stringVal(this.getConfig).+(jobId).+("/")

    /**
      * setting log conf
      *
      * @return
      */
    //    public String getLogConf() {
    //        return logConf;
  }

