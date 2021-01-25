package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset.JISETConfig
import kr.ac.kaist.jiset.spec.ECMAScript
import kr.ac.kaist.jiset.analyzer._

// Analyze phase
case object Analyze extends PhaseObj[ECMAScript, AnalyzeConfig, AbsSemantics] {
  val name = "analyze"
  val help = "performs static anaysis for specifications."

  def apply(
    spec: ECMAScript,
    jisetConfig: JISETConfig,
    config: AnalyzeConfig
  ): AbsSemantics = {
    println(s"--------------------------------------------------")
    println(s"analyzing specification...")
    val worklist = new StackWorklist[ControlPoint]
    val sem = new AbsSemantics
    val fixpoint = new Fixpoint(worklist, sem)
    fixpoint.compute
    sem
  }

  def defaultConfig: AnalyzeConfig = AnalyzeConfig()
  val options: List[PhaseOption[AnalyzeConfig]] = List()
}

// Analyze phase config
case class AnalyzeConfig() extends Config
