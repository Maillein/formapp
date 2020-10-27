package sysdes.formapp.session

import java.net.URLDecoder
import java.util.UUID

import sysdes.formapp.server.{BadRequest, Ok, Response}
import sysdes.formapp.session.SessionServerHandler.states

import scala.collection.mutable

trait gender {
  self: SessionServerHandler =>
  def registerGender(headers: mutable.HashMap[String, String], body: Option[String]): Response = {
    var name: String = ""
    bodyToMap(body) match {
      case Some(value) => name = URLDecoder.decode(value.getOrElse("name", ""), "UTF-8")
    }
    val cookieMap = cookie2Map(headers("Cookie"))
    val sessionID = cookieMap.getOrElse("session-id", "")
    states.get(UUID.fromString(sessionID)) match {
      case Some(state) =>
        state.name = name
        Ok("""<html>
             |<meta charset="UTF-8"/>
             |<body>
             |    <form action="/register-message" method="post">
             |        <label>性別：</label>
             |        <input type="radio" name="gender" value="male"/>
             |        男性
             |        <input type="radio" name="gender" value="female"/>
             |        女性
             |        <br>
             |        <input type="submit" value="next"/>
             |    </form>
             |</body>
             |</html>
             |""".stripMargin)
      case None => BadRequest()
    }
  }
}
