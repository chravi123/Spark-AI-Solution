package com.peak.spark.core

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.support.ClassPathXmlApplicationContext


/**
  * This is useful for other non spring engines (like spark) to access spring beans
  */
object Spring {
   var spring:Spring = spring
   val logger: Logger = LoggerFactory.getLogger(classOf[Spring])

  def bridge(jobId: String): Spring = {
    if (spring == null) spring = new Spring(jobId)
    spring
  }
}

//noinspection ScalaRedundantCast
class Spring(val jobId: String) {
  Spring.logger.info("Initializing spring context [{}] at {} ")
  Spring.logger.info(jobId)
  Spring.logger.info(PeakSystem.getName)
  java.lang.System.setProperty("dp.job.id", jobId)
  ctx = new ClassPathXmlApplicationContext("spring/*.xml")
   var ctx:ClassPathXmlApplicationContext = ctx

  def get(name: String): Object = ctx.getBean(name)

  def get[T](name: String, c: Class[T]): T = ctx.getBean(name).asInstanceOf[T]

  def get[T](c: Class[T]): T = ctx.getBean(c).asInstanceOf[T]
}