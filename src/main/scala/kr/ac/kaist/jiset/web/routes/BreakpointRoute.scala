package kr.ac.kaist.jiset.web.routes

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe._, io.circe.syntax._, io.circe.parser._, io.circe.generic.semiauto._
import kr.ac.kaist.jiset.ir.JsonProtocol._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.web

// breakpoint router
object BreakpointRoute extends FailFastCirceSupport {
  // root router
  def apply(): Route = pathEnd {
    concat(
      // add breakpoint
      post {
        entity(as[Breakpoint]) { bp =>
          web.debugger.addBreak(bp)
          complete(HttpEntity(ContentTypes.`application/json`, "null"))
        }
      },
      // delete breakpoint
      delete {
        entity(as[String]) { opt =>
          web.debugger.rmBreak(opt)
          complete(HttpEntity(ContentTypes.`application/json`, "null"))
        }
      },
      // toggle breakpoint
      put {
        entity(as[String]) { opt =>
          web.debugger.toggleBreak(opt)
          complete(HttpEntity(ContentTypes.`application/json`, "null"))
        }
      }
    )
  }
}

