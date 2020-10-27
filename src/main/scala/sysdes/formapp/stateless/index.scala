package sysdes.formapp.stateless

import sysdes.formapp.server.{Ok, Response}

trait index {
  self: StatelessServerHandler =>
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
}
