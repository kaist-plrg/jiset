package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset.{ JISETConfig, CHECK_ALARM }
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset._
import scala.Console._

// Analyze phase
case object Analyze extends PhaseObj[CFG, AnalyzeConfig, AbsSemantics] {
  val name = "analyze"
  val help = "performs static anaysis for specifications."

  def apply(
    cfg: CFG,
    jisetConfig: JISETConfig,
    config: AnalyzeConfig
  ): AbsSemantics = {
    val AnalyzeConfig(interact, dot, pdf, prune, gc, target, break, repl) = config
    val sem = new AbsSemantics(cfg, target, gc)
    val transfer = new AbsTransfer(sem, interact, prune, break, repl)
    transfer.compute
    if (dot) transfer.dumpCFG(pdf = pdf)

    sem
  }

  def defaultConfig: AnalyzeConfig = AnalyzeConfig()
  val options: List[PhaseOption[AnalyzeConfig]] = List(
    ("interact", BoolOption(c => c.interact = true),
      "progress one step on every enter input"),
    ("dot", BoolOption(c => c.dot = true),
      "dump the analyzed cfg in a dot format"),
    ("pdf", BoolOption(c => { c.dot = true; c.pdf = true }),
      "dump the analyze cfg in a dot and pdf format"),
    ("no-prune", BoolOption(c => c.prune = false),
      "no abstract state pruning"),
    ("no-gc", BoolOption(c => c.gc = false),
      "no garbage collection"),
    ("check-alarm", BoolOption(c => CHECK_ALARM = true),
      "perform pruning"),
    ("target", StrOption((c, s) => c.target = Some(s)),
      "set the target of analysis"),
    ("break", StrOption((c, s) => c.break = Some(s)),
      "set the break point"),
    ("repl", BoolOption(c => c.repl = true),
      "use analyze-repl"),
  )
}

// Analyze phase config
case class AnalyzeConfig(
  var interact: Boolean = false,
  var dot: Boolean = false,
  var pdf: Boolean = false,
  var prune: Boolean = true,
  var gc: Boolean = true,
  var target: Option[String] = None,
  var break: Option[String] = None,
  var repl: Boolean = false
) extends Config
