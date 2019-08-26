package kr.ac.kaist.jiset.phase

import scala.util.{ Try, Success }
import kr.ac.kaist.jiset.JISETConfig
import kr.ac.kaist.jiset.core._
import kr.ac.kaist.jiset.model.Model
import kr.ac.kaist.jiset.util._

// LoadCore phase
case object LoadCore extends PhaseObj[Program, LoadCoreConfig, State] {
  val name: String = "load-core"
  val help: String = "Load Core program into Core State"

  def apply(
    pgm: Program,
    jisetConfig: JISETConfig,
    config: LoadCoreConfig
  ): State = Model.initState.copy(context = Model.initState.context.copy(insts = pgm.insts))

  def defaultConfig: LoadCoreConfig = LoadCoreConfig()
  val options: List[PhaseOption[LoadCoreConfig]] = List()
}

// LoadCore phase config
case class LoadCoreConfig() extends Config
