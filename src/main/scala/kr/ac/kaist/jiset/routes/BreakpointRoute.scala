package kr.ac.kaist.jiset.web.routes

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

// breakpoint router
object BreakpointRoute {
  // addAlgoBreak ( algoName: string, enabled: boolean = true ): void;
  // rmAlgoBreak ( opt: string ): void;
  // toggleAlgoBreak ( opt: string ): void;
  // addJSBreak ( line: number, enabled: boolean = true ): void;
  // rmJSBreak ( opt: string ): void;
  // toggleJSBreak ( opt: string ): void;

  // root router
  def apply(): Route = {
    get {
      complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
    }
  }
}

