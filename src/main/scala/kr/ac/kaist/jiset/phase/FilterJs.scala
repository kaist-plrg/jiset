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
    def getList(path: String, cons: ((String, Int)) => JsProgram): List[JsProgram] =
      readFile(path).split(LINE_SEP).toList.zipWithIndex.map(cons(_))

    // put js programs to filter
    def doPut(desc: String, ps: List[JsProgram]): Unit = {
      // put js program to filter
      ProgressBar(desc, ps).foreach { p => Filter.put(p) }

      // print stats
      if (!jisetConfig.silent) {
        println(s"${Filter.getProgramCount}/${Filter.putCount} for ${Filter.getCoveredSize}")
      }
    }

    // put programs from test262 list
    // doPut(
    //   s"put test262 programs",
    //   getList(s"$DATA_DIR/test262-list", Test262Program.apply)
    // )

    // put programs from JEST list
    doPut(
      s"put jest programs",
      getList(s"$DATA_DIR/jest-list", JestProgram.apply)
    )

    // put programs from custom list
    doPut(
      s"put custom programs",
      getList(s"$DATA_DIR/custom-list", CustomProgram.apply)
    )

    // dump filter result
    if (LOG) Filter.dump()

    // close file handles in editor.Filter
    Filter.close()
  }

  def defaultConfig: FilterJsConfig = FilterJsConfig()
  val options: List[PhaseOption[FilterJsConfig]] = List()
}

// FilterJs phase config
case class FilterJsConfig() extends Config
