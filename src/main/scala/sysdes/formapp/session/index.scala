package sysdes.formapp.session

import java.util.UUID

import sysdes.formapp.server.{Ok, Response, State}
import sysdes.formapp.session.SessionServerHandler.states

trait index {
  self: SessionServerHandler =>
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
}
