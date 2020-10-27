package sysdes.formapp.session

import java.net.Socket
import java.util.UUID

import sysdes.formapp.CommonMethods
import sysdes.formapp.server.{Handler, Server, State}

import scala.collection.mutable

object SessionServer extends Server(8002) {
  override def getHandler(socket: Socket) = new SessionServerHandler(socket)
}

object SessionServerHandler {
  // インスタンス間で共有する内部状態に関する変数・関数はこの中に記述
  var states: mutable.Map[UUID, State] = mutable.HashMap[UUID, State]()
}

class SessionServerHandler(socket: Socket)
    extends Handler(socket)
    with CommonMethods
    with index
    with name
    with gender
    with message
    with check {
  import sysdes.formapp.server.{NotFound, Request, Response}

  def handle(request: Request): Response = request match {
    case Request("GET", "/", _, _, _)                           => index()
    case Request("GET", "/?", _, _, _)                          => index()
    case Request("POST", "/register-name", _, headers, _)       => registerName(headers)
    case Request("POST", "/register-gender", _, headers, body)  => registerGender(headers, body)
    case Request("POST", "/register-message", _, headers, body) => registerMessage(headers, body)
    case Request("POST", "/check", _, headers, body)            => check(headers, body)
    case _                                                      => NotFound(s"Requested resource '${request.path}' for ${request.method} is not found.")
  }

}
