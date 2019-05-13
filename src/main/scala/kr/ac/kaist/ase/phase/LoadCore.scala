package kr.ac.kaist.ase.phase

import scala.util.{ Try, Success }
import kr.ac.kaist.ase.ASEConfig
import kr.ac.kaist.ase.node.core._
import kr.ac.kaist.ase.util._

// LoadCore phase
case object LoadCore extends PhaseObj[Program, LoadCoreConfig, State] {
  val name: String = "load-core"
  val help: String = "Load Core program into Core State"

  def apply(
    pgm: Program,
    aseConfig: ASEConfig,
    config: LoadCoreConfig
  ): State = {
    // Evaluate Core program
    val (initialLocals, initialHeap) = Heap().allocLocals()
    val initialEnv: Env = Env(locals = initialLocals)
    State(
      insts = pgm.insts,
      globals = Map(),
      env = initialEnv,
      heap = initialHeap
    )
  }

  def defaultConfig: LoadCoreConfig = LoadCoreConfig()
  val options: List[PhaseOption[LoadCoreConfig]] = List()
}

// LoadCore phase config
case class LoadCoreConfig() extends Config
