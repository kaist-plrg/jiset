package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.js.{ Parser => JSParser, _ }
import kr.ac.kaist.jiset.js.ast._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.JvmUseful._
import scala.io.Source

// Load phase
case object Load extends Phase[Script, LoadConfig, State] {
  val name = "load"
  val help = "loads a JavaScript AST to the initial IR states."

  def apply(
    script: Script,
    jisetConfig: JISETConfig,
    config: LoadConfig
  ): State = this(script, getFirstFilename(jisetConfig, "load"))

  def apply(script: Script): State = this(script, "unknown")

  def apply(
    script: Script,
    filename: String
  ): State = {
    if (needTarget) setTarget(loadSpec(s"$VERSION_DIR/generated"))
    script match {
      case Script0(bodyOpt, _, _) => Initialize(
        inst = Inst(if (bodyOpt.isDefined) s"app $RESULT = (RunJobs)" else "{}"),
        bodyOpt = bodyOpt,
        filename = filename,
      )
    }
  }

  def defaultConfig: LoadConfig = LoadConfig()
  val options: List[PhaseOption[LoadConfig]] = List()
}

// Parse phase config
case class LoadConfig() extends Config
