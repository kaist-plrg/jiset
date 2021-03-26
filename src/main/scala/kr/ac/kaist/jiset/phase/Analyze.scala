package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset.JISETConfig
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset._
import scala.Console._

// Analyze phase
case object Analyze extends PhaseObj[CFG, AnalyzeConfig, Unit] {
  val name = "analyze"
  val help = "performs static anaysis for specifications."

  def apply(
    cfg: CFG,
    jisetConfig: JISETConfig,
    config: AnalyzeConfig
  ): Unit = {
    init(cfg)
    AbsTransfer.compute
  }

  def defaultConfig: AnalyzeConfig = AnalyzeConfig()
  val options: List[PhaseOption[AnalyzeConfig]] = List(
    ("dot", BoolOption(c => DOT = true),
      "dump the analyzed cfg in a dot format"),
    ("pdf", BoolOption(c => { DOT = true; PDF = true }),
      "dump the analyze cfg in a dot and pdf format"),
    ("no-prune", BoolOption(c => PRUNE = false),
      "no abstract state pruning"),
    ("insens", BoolOption(c => USE_VIEW = false),
      "not use type sensitivity for parameters"),
    ("check-alarm", BoolOption(c => CHECK_ALARM = true),
      "check alarms"),
    ("target", StrOption((c, s) => TARGET = Some(s)),
      "set the target of analysis"),
    ("repl", BoolOption(c => REPL = true),
      "use analyze-repl"),
  )
}

// Analyze phase config
case class AnalyzeConfig() extends Config
