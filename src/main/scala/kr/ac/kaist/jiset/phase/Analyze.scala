package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset.JISETConfig
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.js.ast.Script
import kr.ac.kaist.jiset.spec.NativeHelper._
import kr.ac.kaist.jiset.util.JvmUseful._
import kr.ac.kaist.jiset.util._

// Analyze phase
case object Analyze extends Phase[Script, AnalyzeConfig, AbsSemantics] {
  val name = "analyze"
  val help = "performs static analysis for a given JavaScript program."

  def apply(
    script: Script,
    jisetConfig: JISETConfig,
    config: AnalyzeConfig
  ): AbsSemantics = {
    setSpec(loadSpec(s"$VERSION_DIR/generated"))
    AbsSemantics(script, config.execLevel).fixpoint
  }

  def defaultConfig: AnalyzeConfig = AnalyzeConfig()
  val options: List[PhaseOption[AnalyzeConfig]] = List(
    ("repl", BoolOption(c => USE_REPL = true),
      "use REPL for static analysis."),
    ("exec-level", NumOption((c, k) => {
      if (k < 0 || k > 2) println(s"Invalid execution level: $k")
      c.execLevel = k
    }), "use concrete execution to check soundness."),
    ("gc", BoolOption(c => USE_GC = true),
      "use abstract garbage collection."),
    ("flow-sens", BoolOption(c => FLOW_SENS = true),
      "use flow-sensitivity."),
  )
}

// Analyze phase config
case class AnalyzeConfig(
  var execLevel: Int = 0
) extends Config
