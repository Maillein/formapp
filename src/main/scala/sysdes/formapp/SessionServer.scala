package sysdes.formapp

import java.net.{Socket, URLDecoder}
import java.util.UUID

import sysdes.formapp.SessionServerHandler.states
import sysdes.formapp.server.{BadRequest, Handler, Server, State}

import scala.collection.mutable

object SessionServer extends Server(8002) {
  override def getHandler(socket: Socket) = new SessionServerHandler(socket)
}

object SessionServerHandler {
  // インスタンス間で共有する内部状態に関する変数・関数はこの中に記述
  var states: mutable.Map[UUID, State] = mutable.HashMap[UUID, State]()
}

class SessionServerHandler(socket: Socket) extends Handler(socket) {
  import sysdes.formapp.server.{NotFound, Ok, Request, Response}

  def handle(request: Request): Response = request match {
    case Request("GET", "/", _, _, _)                           => index()
    case Request("GET", "/?", _, _, _)                          => index()
    case Request("POST", "/register-name", _, headers, _)       => registerName(headers)
    case Request("POST", "/register-gender", _, headers, body)  => registerGender(headers, body)
    case Request("POST", "/register-message", _, headers, body) => registerMessage(headers, body)
    case Request("POST", "/check", _, headers, body)            => check(headers, body)
    case _                                                      => NotFound(s"Requested resource '${request.path}' for ${request.method} is not found.")
  }

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

  def index(): Response = {
    val sessionID = UUID.randomUUID()
    val response  = Ok("""<html>
            |<meta charset="UTF-8"/>
            |<body>
            |    <form action="/register-name" method="post">
            |        <input type="submit" value="start" />
            |    </form>
            |</body>
            |</html>""".stripMargin)
    response.addHeader("Set-Cookie", s"session-id=$sessionID")
    states.put(sessionID, new State("", "", ""))
    response
  }

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
