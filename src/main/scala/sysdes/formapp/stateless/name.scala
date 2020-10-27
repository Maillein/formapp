package sysdes.formapp.stateless

import sysdes.formapp.server.{Ok, Response}

trait name {
  self: StatelessServerHandler =>
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

}
