package kr.ac.kaist.ase.phase

import kr.ac.kaist.ase.ASEConfig
import kr.ac.kaist.ase.core._
import kr.ac.kaist.ase.util._

// EvalCore phase
case object EvalCore extends PhaseObj[State, EvalCoreConfig, State] {
  val name: String = "eval-core"
  val help: String = "evaluates JavaScript source files to Core."

  def apply(
    initialSt: State,
    aseConfig: ASEConfig,
    config: EvalCoreConfig
  ): State = (new Interp())(initialSt)

  def defaultConfig: EvalCoreConfig = EvalCoreConfig()
  val options: List[PhaseOption[EvalCoreConfig]] = List()
}

// EvalCore phase config
case class EvalCoreConfig() extends Config
