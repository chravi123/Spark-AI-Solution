package com.peak.spark.core

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.BeansException
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextStartedEvent
import org.springframework.stereotype.Component


/**
  * Just launch the job as per command line argument id
  */
@Component
object SparkAppLauncher {
   val logger: Logger = LoggerFactory.getLogger(classOf[SparkAppLauncher])
}

@Component
class SparkAppLauncher extends ApplicationContextAware with ApplicationListener[ContextStartedEvent] {
  @Value("${dp.job.id}")
  val jobId:String = jobId
  @Value("${dp.job.bean}")
  val jobBean:String = jobBean
   var applicationContext:ApplicationContext = applicationContext

  @throws[BeansException]
  override def setApplicationContext(applicationContext: ApplicationContext): Unit = {
    this.applicationContext = applicationContext
  }

  override def onApplicationEvent(event: ContextStartedEvent): Unit = {
    SparkAppLauncher.logger.info("Invoking {} ", jobBean)
    val job:AbstractSparkJob = applicationContext.getBean(jobBean, classOf[AbstractSparkJob]).setJobId(jobId)
    job.setJobId(jobId)
    job.run()
  }
}