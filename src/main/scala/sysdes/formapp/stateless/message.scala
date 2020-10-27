package sysdes.formapp.stateless

import java.net.URLDecoder

import sysdes.formapp.server.{Ok, Response}

trait message {
  self: StatelessServerHandler =>
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
          |        <input type="hidden" name="name" value="${sanitizing(URLDecoder.decode(name, "UTF-8"))}">
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

}
