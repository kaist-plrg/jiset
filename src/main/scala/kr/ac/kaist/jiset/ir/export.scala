package kr.ac.kaist.jiset.ir

import io.circe._, io.circe.syntax._, io.circe.parser._
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.js.ast._
import kr.ac.kaist.jiset.spec.ECMAScript
import kr.ac.kaist.jiset.spec.JsonProtocol._
import scala.scalajs.js.annotation.JSExportTopLevel

object Export {
  @JSExportTopLevel("eval")
  def eval(script: Script): Unit = {
    val initState = script match {
      case Script0(bodyOpt, _, _) => Initialize(
        inst = Inst(if (bodyOpt.isDefined) s"app $RESULT = (RunJobs)" else "{}"),
        bodyOpt = bodyOpt,
      )
    }
    Runtime(initState)
  }

  @JSExportTopLevel("setSpec")
  def setSpec(raw: String): Unit = {
    for {
      json <- parse(raw)
      spec <- json.as[ECMAScript]
    } yield setTarget(spec)
  }
}
