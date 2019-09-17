package com.peak.spark.core

import java.io.IOException
import java.lang.{System, _}
import java.util
import java.util.{Iterator, Properties}

import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._



/**
  * System gets higher priority all the time, that means any propeties in files can be ovverriden by setting system properties
  * <p>
  * For example:  application.properties has
  * company = peak
  * Then you can start program by java -Dcompany=peak  -jar abc.jar - which will replace "company" to "peak"
  * </p>
  */
object PropertiesFactoryBean extends App {
   val logger: Logger = LoggerFactory.getLogger(classOf[PropertiesFactoryBean])


  //noinspection ScalaDocMissingParameterDescription
  class PropertiesFactoryBean  {
    //def mergeProperties: Properties = mergeProperties

    def mergeProperties = ???

    /**
      * System gets higher priority all the time, that means any propeties in files can be ovverriden by setting system properties
      *
      * If you do not want system property to take precedence then just prefix your property with "final.", example
      *
      * final.spark.core.memrory=8g means no way you can override it
      *
      * @return
      * @throws IOException
      */
    @throws[IOException]
      def createProperties: Properties = {
      PropertiesFactoryBean.logger.info("Merging spring and java system properties")
      val properties: Properties = mergeProperties
      properties.putAll(System.getProperties)
      //now see if there any properties not in system but in app properties files with prefix "final."
      for (keyObj <- properties.keySet.toArray) {
        val key = keyObj.toString
        if (key.startsWith("final.")) properties.setProperty(key.substring(6), properties.getProperty(key))
      }
      properties
    }
  }

}


