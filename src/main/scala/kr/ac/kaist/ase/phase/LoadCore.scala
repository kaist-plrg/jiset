package kr.ac.kaist.ase.phase

import scala.util.{ Try, Success }
import kr.ac.kaist.ase.ASEConfig
import kr.ac.kaist.ase.core._
import kr.ac.kaist.ase.util._

// LoadCore phase
case object LoadCore extends PhaseObj[Program, LoadCoreConfig, State] {
  val name: String = "load-core"
  val help: String = "Load Core program into Core State"

  def apply(
    pgm: Program,
    aseConfig: ASEConfig,
    config: LoadCoreConfig
  ): State = State(
    retValue = None,
    insts = pgm.insts,
    globals = Map(),
    locals = Map(),
    heap = Heap()
  )

  def defaultConfig: LoadCoreConfig = LoadCoreConfig()
  val options: List[PhaseOption[LoadCoreConfig]] = List()
}

// LoadCore phase config
case class LoadCoreConfig() extends Config
