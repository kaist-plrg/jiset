package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.editor._
import kr.ac.kaist.jiset.spec.ECMAScript
import kr.ac.kaist.jiset.util.JvmUseful._
import kr.ac.kaist.jiset.util.ProgressBar

// FilterJs phase
case object FilterJs extends Phase[ECMAScript, FilterJsConfig, Unit] {
  val name: String = "filter-js"
  val help: String = "Filter a set of JS programs for ECMAScript comprehension."

  def apply(
    spec: ECMAScript,
    jisetConfig: JISETConfig,
    config: FilterJsConfig
  ): Unit = {
    // get program list from path
    def getList(path: String): List[(String, Int)] =
      readFile(path).split(LINE_SEP).toList.zipWithIndex

    // put js programs to filter
    def doPut(
      desc: String,
      metas: List[(String, Int)],
      put: ((String, Int)) => Unit
    ): Unit = {
      // put js program to filter
      ProgressBar(desc, metas).foreach(put(_))

      // print stats
      if (!jisetConfig.silent) Filter.printStats()
    }

    // put programs from test262 list
    doPut(
      s"put test262 programs",
      getList(s"$DATA_DIR/test262-list"),
      Filter.putTest262
    )

    // put programs from JEST list
    doPut(
      s"put jest programs",
      getList(s"$DATA_DIR/jest-list"),
      Filter.putJest
    )

    // put programs from custom list
    doPut(
      s"put custom programs",
      getList(s"$DATA_DIR/custom-list"),
      Filter.putCustom
    )

    // dump filter result
    if (LOG) { Filter.dump(); Filter.dumpCsv() }

    // close file handles in editor.Filter
    Filter.close()
  }

  def defaultConfig: FilterJsConfig = FilterJsConfig()
  val options: List[PhaseOption[FilterJsConfig]] = List()
}

// FilterJs phase config
case class FilterJsConfig() extends Config
