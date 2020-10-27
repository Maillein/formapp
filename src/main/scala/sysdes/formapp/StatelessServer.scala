package sysdes.formapp

import java.net.{Socket, URLDecoder}

import sysdes.formapp.server.{Handler, Server}

import scala.collection.mutable

object StatelessServer extends Server(8001) {
  override def getHandler(socket: Socket) = new StatelessServerHandler(socket)
}

class StatelessServerHandler(socket: Socket) extends Handler(socket) {

  import sysdes.formapp.server.{NotFound, Ok, Request, Response}

  override def handle(request: Request): Response = request match {
    case Request("GET", "/", _, _, _)                     => index()
    case Request("GET", "/?", _, _, _)                    => index()
    case Request("POST", "/register-name", _, _, _)       => registerName()
    case Request("POST", "/register-gender", _, _, body)  => registerGender(body)
    case Request("POST", "/register-message", _, _, body) => registerMessage(body)
    case Request("POST", "/check", _, _, body)            => check(body)
    case _ => NotFound(s"Requested resource " +
            s"'${request.path}' for ${request.method} is not found.")
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

  def index(): Response = {
    Ok("""<html>
        |<meta charset="UTF-8"/>
        |<body>
        |    <form action="/register-name" method="post">
        |        <input type="submit" value="start" />
        |    </form>
        |</body>
        |</html>""".stripMargin)
  }

  def registerName(): Response = {
    Ok("""<html>
          |<meta charset="UTF-8"/>
          |<body>
          |    <form action="/register-gender" method="post">
          |        <label>名前：</label>
          |        <input type="text" name="name">
          |        <br>
          |        <input type="submit" value="next">
          |    </form>
          |</body>
          |</html>
          |""".stripMargin)
  }

  def registerGender(body: Option[String]): Response = {
    val bodyMap      = bodyToMap(body)
    var name: String = ""
    bodyMap match {
      case Some(value) => name = value.getOrElse("name", "")
    }
    Ok(s"""<html>
            |<meta charset="UTF-8"/>
            |<body>
            |    <form action="/register-message" method="post">
            |        <input type="hidden" name="name" value="${URLDecoder.decode(name, "UTF-8")}">
            |        <label>性別：</label>
            |        <input type="radio" name="gender" value="male">
            |        男性
            |        <input type="radio" name="gender" value="female">
            |        女性
            |        <br>
            |        <input type="submit" value="next">
            |    </form>
            |</body>
            |</html>
            |""".stripMargin)
  }

  def registerMessage(body: Option[String]): Response = {
    val bodyMap        = bodyToMap(body)
    var name: String   = ""
    var gender: String = ""
    bodyMap match {
      case Some(value) =>
        name = value.getOrElse("name", "")
        gender = value.getOrElse("gender", "")
    }
    Ok(s"""<html>
           |<meta charset="UTF-8"/>
           |<body>
           |    <form action="/check" method="post">
           |        <input type="hidden" name="name" value="${URLDecoder.decode(name, "UTF-8")}">
           |        <input type="hidden" name="gender" value="$gender">
           |        <label>メッセージ：</label>
           |        <br>
           |        <textarea name="message" rows="4" cols="40"></textarea>
           |        <br>
           |        <input type="submit" value="next">
           |    </form>
           |</body>
           |</html>
           |""".stripMargin)
  }

  def check(body: Option[String]): Response = {
    val bodyMap         = bodyToMap(body)
    var name: String    = ""
    var gender: String  = ""
    var message: String = ""
    bodyMap match {
      case Some(value) =>
        name = value.getOrElse("name", "")
        gender = value.getOrElse("gender", "")
        message = value.getOrElse("message", "")
    }
    Ok(s"""<html>
           |<meta charset="UTF-8"/>
           |<body>
           |    <form action="/" method="get">
           |        <label>名前：</label>
           |        <span id="send-name">${URLDecoder.decode(name, "UTF-8")}</span>
           |        <br>
           |        <label>性別：</label>
           |        <span id="send-gender">$gender</span>
           |        <br>
           |        <label>メッセージ：</label>
           |        <br>
           |        <textarea name="send-message" disabled>${URLDecoder.decode(message, "UTF-8")}</textarea>
           |        <br>
           |        <input type="submit" value="submit"/>
           |    </form>
           |</body>
           |</html>
           |""".stripMargin)
  }

}
