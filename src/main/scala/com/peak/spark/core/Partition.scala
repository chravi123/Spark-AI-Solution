package com.peak.spark.core


//noinspection ScalaDocMissingParameterDescription
trait Partition {
  /**
    * Implement how ever you want
    *
    * @param prefix
    * @param value
    * @return
    */
  def getUri(prefix: String, value: String, valueFormat: String): String
}
