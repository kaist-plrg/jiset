package kr.ac.kaist.jiset.phase

import scala.util.{ Try, Success }
import kr.ac.kaist.jiset.JISETConfig
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util._

// IRLoad phase
case object IRLoad extends Phase[Program, IRLoadConfig, State] {
  val name: String = "load-ir"
  val help: String = "loads an IR AST to the initial IR states."

  def apply(
    program: Program,
    jisetConfig: JISETConfig,
    config: IRLoadConfig
  ): State = State(InstCursor).moveTo(program)

  def defaultConfig: IRLoadConfig = IRLoadConfig()
  val options: List[PhaseOption[IRLoadConfig]] = List()
}

// IRLoad phase config
case class IRLoadConfig() extends Config
