package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.editor._
import kr.ac.kaist.jiset.spec.ECMAScript
import kr.ac.kaist.jiset.util.JvmUseful._

// FilterJs phase
case object FilterJs extends Phase[ECMAScript, FilterJsConfig, Unit] {
  val name: String = "filter-js"
  val help: String = "Filter a set of JS programs for ECMAScript comprehension."

  def apply(
    spec: ECMAScript,
    jisetConfig: JISETConfig,
    config: FilterJsConfig
  ): Unit = {
    List(EDITOR_LOG_DIR, EDITOR_CACHED_DIR).foreach(mkdir(_))
    // load program set from test262, jest, custom lists
    val total = List(
      // SimpleProgramSet.fromTest262(),
      SimpleProgramSet.fromJest(),
      SimpleProgramSet.fromCustom()
    )
      .reduce(_ union _)
      .setDumpDir(s"$EDITOR_LOG_DIR/total")
    if (LOG) total.dumpStats()

    // filtered program set
    val filtered =
      FilteredProgramSet(total).setDumpDir(s"$EDITOR_LOG_DIR/filtered")
    if (LOG) filtered.dumpStats()
    filtered.dump()
  }

  def defaultConfig: FilterJsConfig = FilterJsConfig()
  val options: List[PhaseOption[FilterJsConfig]] = List()
}

// FilterJs phase config
case class FilterJsConfig() extends Config
