package kr.ac.kaist.jiset.js

import kr.ac.kaist.jiset.util.BasicJsonProtocol
import kr.ac.kaist.jiset.js.Debugger._
import io.circe._, io.circe.generic.semiauto._, io.circe.generic.auto._
import io.circe.syntax._

object JsonProtocol extends BasicJsonProtocol {
  implicit lazy val StepResultDecoder: Decoder[StepResult] = deriveDecoder
  implicit lazy val StepResultEncoder: Encoder[StepResult] = deriveEncoder
}
