package sysdes.formapp.stateless

import java.net.Socket

import sysdes.formapp.CommonMethods
import sysdes.formapp.server.{Handler, Server}

object StatelessServer extends Server(8001) {
  override def getHandler(socket: Socket) = new StatelessServerHandler(socket)
}

class StatelessServerHandler(socket: Socket)
    extends Handler(socket)
    with CommonMethods
    with index
    with name
    with gender
    with message
    with check {

  import sysdes.formapp.server.{NotFound, Request, Response}

  override def handle(request: Request): Response = request match {
    case Request("GET", "/", _, _, _)                     => index()
    case Request("GET", "/?", _, _, _)                    => index()
    case Request("POST", "/register-name", _, _, _)       => registerName()
    case Request("POST", "/register-gender", _, _, body)  => registerGender(body)
    case Request("POST", "/register-message", _, _, body) => registerMessage(body)
    case Request("POST", "/check", _, _, body)            => check(body)
    case _ =>
      NotFound(
        s"Requested resource " +
        s"'${request.path}' for ${request.method} is not found."
      )
  }

}
