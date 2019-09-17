package com.peak.spark.core

import org.springframework.stereotype.Component


/**
  * Default implementation.
  */
@Component("voidPartition")
object VoidPartition extends Partition {
  override def getUri(prefix: String, value: String, valueFormat: String): String = prefix
}
