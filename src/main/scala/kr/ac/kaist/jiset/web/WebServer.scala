package kr.ac.kaist.jiset.web

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import kr.ac.kaist.jiset.web.routes._
import scala.io.StdIn

object WebServer {
  def run(port: Int): Unit = {
    implicit val system = ActorSystem(Behaviors.empty, "jiset-web")
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.executionContext

    // root router
    val rootRoute = cors() {
      concat(
        path("spec")(SpecRoute()), // spec route
        path("state")(StateRoute()), // state route
        path("exec")(ExecRoute()), // exec route
        path("breakpoint")(BreakpointRoute()), // breakpoint route
      )
    }

    val bindingFuture = Http().newServerAt("localhost", port).bind(rootRoute)

    println(s"Server now online at port $port.\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}
