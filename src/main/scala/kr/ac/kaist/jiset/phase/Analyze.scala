package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset.JISETConfig
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.spec.ECMAScript
import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.util.Useful.time

// Analyze phase
case object Analyze extends PhaseObj[ECMAScript, AnalyzeConfig, AbsSemantics] {
  val name = "analyze"
  val help = "performs static anaysis for specifications."

  def apply(
    spec: ECMAScript,
    jisetConfig: JISETConfig,
    config: AnalyzeConfig
  ): AbsSemantics = {
    time(s"analyze specification", {
      val cfg = CFG(spec)
      val grammar = spec.grammar
      val sem = new AbsSemantics(cfg, grammar)
      val fixpoint = new Fixpoint(sem)
      fixpoint.compute
      sem
    })
  }

  def defaultConfig: AnalyzeConfig = AnalyzeConfig()
  val options: List[PhaseOption[AnalyzeConfig]] = List()
}

// Analyze phase config
case class AnalyzeConfig() extends Config
