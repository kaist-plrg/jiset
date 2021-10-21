package kr.ac.kaist.jiset.test262

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.ir.Logger
import kr.ac.kaist.jiset.js.Test262
import kr.ac.kaist.jiset.js.ast.Script

class EvalLargeTest extends Test262Test {
  val name: String = "test262Test"

  // logging execution traces
  LOG = true

  // set base directory for ir.Logger
  Logger.setBase(s"$logDir/logger")

  // test test262 test
  def doTest(script: Script, name: String): Unit =
    evalTest(script, name)

  // registration
  def init: Unit = check(name, { test262Test("eval") })
  init
}
