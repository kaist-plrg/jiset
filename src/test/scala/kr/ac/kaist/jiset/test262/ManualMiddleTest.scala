package kr.ac.kaist.jiset.test262

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.ir.Logger
import kr.ac.kaist.jiset.js.Test262
import kr.ac.kaist.jiset.js.ast.Script
import kr.ac.kaist.jiset.util.JvmUseful._

class ManualMiddleTest extends Test262Test {
  val name: String = "test262ManualTest"

  // logging execution traces
  LOG = true

  // set base directory for ir.Logger
  Logger.setBase(s"$logDir/logger")

  // filename for manual target names
  val filename = s"$BASE_DIR/tests/manual-test262"

  // eval-manual targets
  val names = readFile(filename).split(LINE_SEP).toSet
  override val targets = Test262.config.normal.filter(names contains _.name)

  // test test262 test
  def doTest(script: Script, name: String): Unit =
    evalTest(script, name)

  // registration
  def init: Unit = check(name, { test262Test("eval-manual") })
  init
}
