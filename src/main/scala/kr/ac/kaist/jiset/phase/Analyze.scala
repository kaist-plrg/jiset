package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset.JISETConfig
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset._

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
    val transfer = new AbsTransfer(sem, config.interact, config.dot, config.pdf)
    transfer.compute
    sem
  }

  def defaultConfig: AnalyzeConfig = AnalyzeConfig()
  val options: List[PhaseOption[AnalyzeConfig]] = List(
    ("interact", BoolOption(c => c.interact = true),
      "progress one step on every enter input"),
    ("dot", BoolOption(c => c.dot = true),
      "dump the analyzed cfg in a dot format"),
    ("pdf", BoolOption(c => { c.dot = true; c.pdf = true }),
      "dump the analyze cfg in a dot and pdf format")
  )
}

// Analyze phase config
case class AnalyzeConfig(
  var interact: Boolean = false,
  var dot: Boolean = false,
  var pdf: Boolean = false
) extends Config
