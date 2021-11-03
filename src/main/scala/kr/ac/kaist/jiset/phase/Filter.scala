package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.editor._
import kr.ac.kaist.jiset.spec.ECMAScript
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util.JvmUseful._

// Filter phase
case object Filter extends Phase[ECMAScript, FilterConfig, FilteredProgramSet] {
  val name: String = "filter"
  val help: String = "Filter a set of JS programs for ECMAScript comprehension."

  def apply(
    spec: ECMAScript,
    jisetConfig: JISETConfig,
    config: FilterConfig
  ): FilteredProgramSet = {
    val fset = if (!config.load) {
      List(EDITOR_LOG_DIR, EDITOR_CACHED_DIR).foreach(mkdir(_))
      // load program set from test262, jest, custom lists
      val total = List(
        SimpleProgramSet.fromTest262(),
        SimpleProgramSet.fromJest(),
        SimpleProgramSet.fromCustom()
      )
        .reduce(_ union _)
        .setDumpDir(s"$EDITOR_LOG_DIR/total")
      if (LOG) total.dumpStats()

      // filtered program set
      val filtered = FilteredProgramSet(total).setDumpDir(FILTERED_DIR)
      filtered.dump()
      filtered
    } else {
      val (_, filtered) = time(
        s"loading filtered data from $FILTERED_DIR",
        FilteredProgramSet.load(FILTERED_DIR)
      )
      filtered
    }
    if (LOG) fset.dumpStats()
    fset.printStats()
    fset
  }

  def defaultConfig: FilterConfig = FilterConfig()
  val options: List[PhaseOption[FilterConfig]] = List(
    ("load", BoolOption(c => c.load = true),
      "load a filtered program set"),
  )
}

// Filter phase config
case class FilterConfig(
  var load: Boolean = false
) extends Config
