package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.editor.Filter
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
    import Filter._

    // get program list from path
    def getList(path: String): List[String] =
      readFile(path).split(LINE_SEP).toList

    // put programs from test262 list
    ProgressBar(
      s"put test262 programs",
      getList(s"$DATA_DIR/test262-list").zipWithIndex
    ).foreach {
        case (name, id) =>
          put(Test262Program(id, s"$TEST262_TEST_DIR/$name"))
      }

    // put programs from JEST list
    ProgressBar(
      s"put jest programs",
      getList(s"$DATA_DIR/jest-list").zipWithIndex
    ).foreach {
        case (name, id) =>
          put(JestProgram(id, s"$DATA_DIR/jest/$name"))
      }

    // put programs from custom list
    ProgressBar(
      s"put custom programs",
      getList(s"$DATA_DIR/custom-list").zipWithIndex
    ).foreach {
        case (name, id) =>
          put(CustomProgram(id, s"$DATA_DIR/custom/$name"))
      }

    // close file handles in editor.Filter
    Filter.close()
  }

  def defaultConfig: FilterJsConfig = FilterJsConfig()
  val options: List[PhaseOption[FilterJsConfig]] = List()
}

// FilterJs phase config
case class FilterJsConfig() extends Config
