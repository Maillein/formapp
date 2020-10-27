package sysdes.formapp.session

import java.net.URLDecoder
import java.util.UUID

import sysdes.formapp.server.{BadRequest, Ok, Response}
import sysdes.formapp.session.SessionServerHandler.states

import scala.collection.mutable

trait check {
  self: SessionServerHandler =>
  def check(headers: mutable.HashMap[String, String], body: Option[String]): Response = {
    var message: String = ""
    bodyToMap(body) match {
      case Some(value) => message = URLDecoder.decode(value.getOrElse("message", ""), "UTF-8")
    }
    val cookieMap = cookie2Map(headers("Cookie"))
    val sessionID = cookieMap.getOrElse("session-id", "")
    states.get(UUID.fromString(sessionID)) match {
      case Some(state) =>
        state.message = message
        val response = Ok(s"""<html>
                             |<meta charset="UTF-8"/>
                             |<body>
                             |    <form action="/" method="get">
                             |        <label>名前：</label>
                             |        <span name="send-name">${sanitizing(state.name)}</span>
                             |        <br>
                             |        <label>性別：</label>
                             |        <span name="send-gender">${sanitizing(state.gender)}</span>
                             |        <br>
                             |        <label>メッセージ：</label>
                             |        <br>
                             |        <textarea name="send-message" disabled>${sanitizing(state.message)}</textarea>
                             |        <br>
                             |        <input type="submit" value="submit"/>
                             |    </form>
                             |</body>
                             |</html>
                             |""".stripMargin)
        response.addHeader("Set-Cookie", s"session-id=$sessionID; Max-Age=0")
        response
      case None => BadRequest()
    }
  }

}
