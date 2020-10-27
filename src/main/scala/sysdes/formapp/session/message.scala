package sysdes.formapp.session

import java.util.UUID

import sysdes.formapp.server.{BadRequest, Ok, Response}
import sysdes.formapp.session.SessionServerHandler.states

import scala.collection.mutable

trait message {
  self: SessionServerHandler =>
  def registerMessage(headers: mutable.HashMap[String, String], body: Option[String]): Response = {
    var gender = ""
    bodyToMap(body) match {
      case Some(value) =>
        gender = value.getOrElse("gender", "")
    }
    val cookieMap = cookie2Map(headers("Cookie"))
    val sessionID = cookieMap.getOrElse("session-id", "")
    states.get(UUID.fromString(sessionID)) match {
      case Some(state) =>
        state.gender = gender
        Ok("""<html>
             |<meta charset="UTF-8"/>
             |<body>
             |    <form action="/check" method="post">
             |        <label>メッセージ：</label>
             |        <br>
             |        <textarea name="message" rows="4" cols="40"></textarea>
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
