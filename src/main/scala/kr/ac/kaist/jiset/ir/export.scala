package kr.ac.kaist.jiset.ir

import io.circe._, io.circe.syntax._
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.spec.ECMAScript
import kr.ac.kaist.jiset.spec.JsonProtocol._
import scala.scalajs.js.annotation.JSExportTopLevel

object Export {
  @JSExportTopLevel("interp")
  def interp(str: String): Unit = {
    Interp(State())
  }

  @JSExportTopLevel("setSpec")
  def setSpec(json: Json): Unit = {
    json.as[ECMAScript] match {
      case Left(err) => throw err
      case Right(spec) => setTarget(spec)
    }
  }
}
