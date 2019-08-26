package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset.JISETConfig
import kr.ac.kaist.jiset.core._
import kr.ac.kaist.jiset.util._

// EvalCore phase
case object EvalCore extends PhaseObj[State, EvalCoreConfig, State] {
  val name: String = "eval-core"
  val help: String = "evaluates JavaScript source files to Core."

  def apply(
    initialSt: State,
    jisetConfig: JISETConfig,
    config: EvalCoreConfig
  ): State = (new Interp())(initialSt)

  def defaultConfig: EvalCoreConfig = EvalCoreConfig()
  val options: List[PhaseOption[EvalCoreConfig]] = List()
}

// EvalCore phase config
case class EvalCoreConfig() extends Config
