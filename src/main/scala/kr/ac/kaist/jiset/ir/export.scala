package kr.ac.kaist.jiset.ir

import io.circe._, io.circe.syntax._, io.circe.parser._
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.js.ast._
import kr.ac.kaist.jiset.spec.ECMAScript
import kr.ac.kaist.jiset.spec.JsonProtocol._
import scala.scalajs.js.annotation._

object Export {
  @JSExportTopLevel("Scala_WebDebugger")
  @JSExportAll
  class WebDebugger(override val st: State) extends Debugger {
    detail = true
    DEBUG = true
    def _step() = step
    def _stepOver() = stepOver
    def _stepOut() = stepOut
    def getAlgoName(): String = {
      currentAlgo match {
        case Some(algo) => algo.name
        case None => ""
      }
    }
    def getLine(): Int = currentInst match {
      case Some(i) => i.line.getOrElse(-1)
      case None => -1
    }
    def getStackInfo(): String = {
      val infos: List[String] = (st.context :: st.ctxtStack).map(_.name)
      infos.asJson.noSpaces
    }
  }

  @JSExportTopLevel("Scala_initializeState")
  def initializeDebugger(compressed: String): State = {
    val json = parse(compressed) match {
      case Left(err) => throw err
      case Right(json) => json
    }
    val script = Script(json)
    Initialize(script)
  }

  @JSExportTopLevel("Scala_setSpec")
  def setSpec(raw: String): Unit = for {
    json <- parse(raw)
    spec <- json.as[ECMAScript]
  } yield setTarget(spec)
}
