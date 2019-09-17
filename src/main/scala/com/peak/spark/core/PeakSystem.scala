package com.peak.spark.core

import java.net.InetAddress


object PeakSystem {
   var name:String = name

  /**
    * get system name
    *
    * @return
    */
  def getName: String = try {
    if (name == null) name = InetAddress.getLocalHost.getHostName
    name
  } catch {
    case _: Exception =>
      "unknown"
  }

  /**
    * get host address (ip)
    *
    * @return
    */
  def getIP: String = try
    InetAddress.getLocalHost.getHostAddress
  catch {
    case _: Exception =>
      "unknown"
  }
}
