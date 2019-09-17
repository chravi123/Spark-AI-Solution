package com.peak.spark.jobs

import java.util.Properties

//noinspection ScalaUnusedExpression,ScalaDocMissingParameterDescription
object SparkConfigKeys extends Enumeration {
  type SparkConfigKeys = Value


  val DP_SPARK_JOB_ID, DP_SPARK_JOB_TEMPDIR, DP_OUTPUT_PARTITIONER = Value
   var key:String = key
  var defaultValue: Object= defaultValue


  /**
    * Constructor for SparkConfigKeys
    *
    * @param key          Configuration key
    * @param defaultValue Default value for the configuration
    */
  def SparkConfigKeys(key: String, defaultValue: Object) {

    this.key = key
    this.defaultValue = defaultValue
  }

  /**
    * Constructor for SparkConfigKeys
    *
    * @param key Configuration key
    */
  def SparkConfigKeys(key: String) {
    SparkConfigKeys(key, null)
  }

  @Override
  override def toString(): String = key

  /**
    * return String value
    *
    * @param props
    * @return String
    */
  def stringVal(props: Properties): String =
    if (org.springframework.util.StringUtils.isEmpty(props.getProperty(key))) defaultValue.asInstanceOf[String]
  else props.getProperty(key)

  /**
    * return int value
    *
    * @param props
    * @return int
    */
  def intVal(props: Properties): Int = this.stringVal(props).toInt

  /**
    * return long value
    *
    * import scala.annotation.meta.param
    * props
    *
    * @return long
    */
  def longVal(props: Properties):Long = this.stringVal(props).toLong



  /**
    * return boolean value
    * import scala.annotation.meta.param
    * props
    *
    * @return boolean
    */
  def boolVal(props: Properties): Boolean = this.stringVal(props).toBoolean


  def defaultVal: String =
    defaultValue.asInstanceOf[String]
}
