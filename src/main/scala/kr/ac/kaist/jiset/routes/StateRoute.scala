package kr.ac.kaist.jiset.web.routes

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

// state router
object StateRoute {
  // // get state info
  // getStackFrame (): string;
  // getHeap (): string;
  // getEnv (): string;
  // getJsRange (): string;

  // initialize state by input JS program
  def init(): Route = ???

  // root router
  def apply(): Route = {
    get {
      complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
    }
  }
}
