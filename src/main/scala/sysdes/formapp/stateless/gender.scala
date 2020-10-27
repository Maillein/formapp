package sysdes.formapp.stateless

import java.net.URLDecoder

import sysdes.formapp.server.{Ok, Response}

trait gender {
  self: StatelessServerHandler =>
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
          |        <input type="hidden" name="name" value="${sanitizing(URLDecoder.decode(name, "UTF-8"))}">
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

}
