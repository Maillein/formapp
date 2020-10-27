package sysdes.formapp.session

import java.util.UUID

import sysdes.formapp.server.{BadRequest, Ok, Response}
import sysdes.formapp.session.SessionServerHandler.states

import scala.collection.mutable

trait name {
  self: SessionServerHandler =>
  def registerName(headers: mutable.HashMap[String, String]): Response = {
    val cookieMap = cookie2Map(headers("Cookie"))
    val sessionID = cookieMap.getOrElse("session-id", "")
    states.get(UUID.fromString(sessionID)) match {
      case Some(_) => Ok("""<html>
                           |<meta charset="UTF-8"/>
                           |<body>
                           |    <form action="/register-gender" method="post">
                           |        <label>名前：</label>
                           |        <input type="text" name="name"/>
                           |        <br>
                           |        <input type="submit" value="next"/>
                           |    </form>
                           |</body>
                           |</html>
                           |""".stripMargin)
      case None    => BadRequest()
    }
  }
}
