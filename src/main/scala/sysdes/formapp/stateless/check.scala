package sysdes.formapp.stateless

import java.net.URLDecoder

import sysdes.formapp.server.{Ok, Response}

trait check {
  self: StatelessServerHandler =>
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
          |        <span id="send-name">${sanitizing(URLDecoder.decode(name, "UTF-8"))}</span>
          |        <br>
          |        <label>性別：</label>
          |        <span id="send-gender">$gender</span>
          |        <br>
          |        <label>メッセージ：</label>
          |        <br>
          |        <textarea name="send-message" disabled>${sanitizing(URLDecoder.decode(message, "UTF-8"))}</textarea>
          |        <br>
          |        <input type="submit" value="submit"/>
          |    </form>
          |</body>
          |</html>
          |""".stripMargin)
  }

}
