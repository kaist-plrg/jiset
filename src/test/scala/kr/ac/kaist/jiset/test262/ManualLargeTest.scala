package kr.ac.kaist.jiset.test262

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.js.Test262
import kr.ac.kaist.jiset.util.JvmUseful._

class ManualLargeTest extends Test262Test {
  val name: String = "test262ManualTest"

  // filename for manual target names
  val filename = s"$BASE_DIR/tests/manual-test262"

  // registration
  def init: Unit = check(name, {
    val manuals = readFile(filename).split(LINE_SEP).toSet
    val targets = Test262.config.normal.filter(manuals contains _.name)
    test262EvalTest(targets, "eval-manual")
  })
  init
}
