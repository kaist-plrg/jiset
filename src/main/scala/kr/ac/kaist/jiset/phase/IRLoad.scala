package kr.ac.kaist.jiset.phase

import scala.util.{ Try, Success }
import kr.ac.kaist.jiset.JISETConfig
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util._

// IRLoad phase
case object IRLoad extends PhaseObj[Program, IRLoadConfig, State] {
  val name: String = "load-ir"
  val help: String = "Load IR program into IR State"

  def apply(
    program: Program,
    jisetConfig: JISETConfig,
    config: IRLoadConfig
  ): State = State(program)

  def defaultConfig: IRLoadConfig = IRLoadConfig()
  val options: List[PhaseOption[IRLoadConfig]] = List()
}

// IRLoad phase config
case class IRLoadConfig() extends Config
