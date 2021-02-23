package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset.JISETConfig
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.util.Useful.time

// Analyze phase
case object Analyze extends PhaseObj[CFG, AnalyzeConfig, AbsSemantics] {
  val name = "analyze"
  val help = "performs static anaysis for specifications."

  def apply(
    cfg: CFG,
    jisetConfig: JISETConfig,
    config: AnalyzeConfig
  ): AbsSemantics = {
    val sem = new AbsSemantics(cfg)
    val fixpoint = new Fixpoint(sem)
    fixpoint.compute
    sem
  }

  def defaultConfig: AnalyzeConfig = AnalyzeConfig()
  val options: List[PhaseOption[AnalyzeConfig]] = List()
}

// Analyze phase config
case class AnalyzeConfig() extends Config
