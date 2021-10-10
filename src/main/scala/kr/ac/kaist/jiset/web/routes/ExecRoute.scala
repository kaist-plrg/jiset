package kr.ac.kaist.jiset.web.routes

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe._, io.circe.syntax._, io.circe.parser._, io.circe.generic.semiauto._
import kr.ac.kaist.jiset.web

// exec router
object ExecRoute extends FailFastCirceSupport {
  // // ir steps
  // irStep (): Scala_StepResult;
  // irStepOver (): Scala_StepResult;
  // irStepOut (): Scala_StepResult;
  // // spec steps
  // specStep (): Scala_StepResult;
  // specStepOver (): Scala_StepResult;
  // specStepOut (): Scala_StepResult;
  // // spec steps
  // jsStep (): Scala_StepResult;
  // // continue
  // continueAlgo (): Scala_StepResult;

  // parameters for `/exec/run`
  case class RunParams(bps: String, compressed: String)
  implicit lazy val RunParamsDecoder: Decoder[RunParams] = deriveDecoder
  implicit lazy val RunParamsEncoder: Encoder[RunParams] = deriveEncoder

  // root router
  def apply(): Route = {
    post {
      path("run") {
        entity(as[RunParams]) {
          // TODO handle initial breakpoints
          case RunParams(_, compressed) => {
            // initialize debugger
            web.setDebugger(compressed)
            complete(HttpEntity(ContentTypes.`application/json`, "null"))
          }
        }
      }
    }
  }
}
