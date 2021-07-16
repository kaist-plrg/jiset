package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.{ LINE_SEP, JISETConfig }

// IRREPL phase
case object IRREPL extends Phase[State, IRREPLConfig, Unit] {
  val name = "repl-ir"
  val help = "performs REPL for IR instructions."

  def apply(
    st: State,
    jisetConfig: JISETConfig,
    config: IRREPLConfig
  ): Unit = REPL(st)

  def defaultConfig: IRREPLConfig = IRREPLConfig()
  val options: List[PhaseOption[IRREPLConfig]] = List()
}

// IRREPL phase config
case class IRREPLConfig() extends Config
