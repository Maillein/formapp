package sysdes.formapp

import scala.collection.mutable

trait CommonMethods {
  def bodyToMap(body: Option[String]): Option[mutable.HashMap[String, String]] = {
    val map: mutable.HashMap[String, String] = new mutable.HashMap()
    body match {
      case Some(value) =>
        value
          .split("&")
          .foreach(line => {
            val p = """(.*)=(.*)""".r
            line match {
              case p(k, v) => map put (k, v)
            }
          })
        Some(map)
      case None => None
    }
  }

  def cookie2Map(cookie: String): mutable.HashMap[String, String] = {
    val map: mutable.HashMap[String, String] = new mutable.HashMap()
    cookie
      .split("; ")
      .foreach(line => {
        val p = """(.*)=(.*)""".r
        line match {
          case p(k, v) => map put (k, v)
          case _       => None
        }
      })
    map
  }

  def sanitizing(str: String): String = {
    val ret = new StringBuilder()
    str.foreach {
      case '&'  => ret.addAll("&amp;")
      case '<'  => ret.addAll("&lt;")
      case '>'  => ret.addAll("&gt;")
      case '"'  => ret.addAll("&quot;")
      case '\'' => ret.addAll("&#39;")
      case c    => ret.addOne(c)
    }
    ret.toString()
  }
}
