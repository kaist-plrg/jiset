package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.editor._
import kr.ac.kaist.jiset.spec.ECMAScript
import kr.ac.kaist.jiset.util.JvmUseful._

// Reduce phase
case object Reduce extends Phase[FilteredProgramSet, ReduceConfig, Unit] {
  val name: String = "reduce"
  val help: String = "Reduce a set of JS programs for ECMAScript comprehension."

  def apply(
    fset: FilteredProgramSet,
    jisetConfig: JISETConfig,
    config: ReduceConfig
  ): Unit = {
    fset.printStats(detail = true)
    Reducer(fset)
    fset.printStats(detail = true)
    fset.setDumpDir(REDUCED_DIR).dump()
  }

  def defaultConfig: ReduceConfig = ReduceConfig()
  val options: List[PhaseOption[ReduceConfig]] = List()
}

// Reduce phase config
case class ReduceConfig() extends Config
