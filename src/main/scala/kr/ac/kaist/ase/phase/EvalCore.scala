package kr.ac.kaist.ase.phase

import kr.ac.kaist.ase.ASEConfig
import kr.ac.kaist.ase.node.core._
import kr.ac.kaist.ase.util._
import scala.util.{ Try, Success }

// EvalCore phase
case object EvalCore extends PhaseObj[State, EvalCoreConfig, State] {
  val name: String = "core-interpreter"
  val help: String = "evaluates JavaScript source files to Core."

  def apply(
    initialSt: State,
    aseConfig: ASEConfig,
    config: EvalCoreConfig
  ): State = Interp.fixpoint(initialSt)

  def defaultConfig: EvalCoreConfig = EvalCoreConfig()
  val options: List[PhaseOption[EvalCoreConfig]] = List()
}

// EvalCore phase config
case class EvalCoreConfig() extends Config
