package kr.ac.kaist.jiset.web.routes

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe._, io.circe.syntax._, io.circe.parser._, io.circe.generic.semiauto._
import kr.ac.kaist.jiset.web
import kr.ac.kaist.jiset.js.JsonProtocol._
import kr.ac.kaist.jiset.ir.JsonProtocol._
import kr.ac.kaist.jiset.ir.Breakpoint
import kr.ac.kaist.jiset.js._

// exec router
object ExecRoute extends FailFastCirceSupport {
  // TODO JS steps

  // parameters for `/exec/run`
  case class RunParams(breakpoints: List[Breakpoint], compressed: String)
  implicit lazy val RunParamsDecoder: Decoder[RunParams] = deriveDecoder
  implicit lazy val RunParamsEncoder: Encoder[RunParams] = deriveEncoder

  // implicit converter for HTTP response
  implicit def int2str(n: Int): String = n.toString
  implicit def res2str(result: Debugger.StepResult): String = result.asJson.noSpaces

  // root router
  def apply(): Route = {
    post {
      concat(
        // initialize debugger with breakpoints and JS code
        path("run") {
          entity(as[RunParams]) {
            case RunParams(bps, compressed) => {
              println(bps, compressed)
              web.setDebugger(bps, compressed)
              complete(HttpEntity(ContentTypes.`application/json`, "null"))
            }
          }
        },
        // spec step
        path("specStep") {
          val result = web.debugger.specStep();
          complete(HttpEntity(ContentTypes.`application/json`, result))
        },
        // spec step-over
        path("specStepOver") {
          val result = web.debugger.specStepOver();
          complete(HttpEntity(ContentTypes.`application/json`, result))
        },
        // spec step-out
        path("specStepOut") {
          val result = web.debugger.specStepOut();
          complete(HttpEntity(ContentTypes.`application/json`, result))
        },
        // spec continue
        path("specContinue") {
          val result = web.debugger.specContinue();
          complete(HttpEntity(ContentTypes.`application/json`, result))
        }
      )
    }
  }
}
