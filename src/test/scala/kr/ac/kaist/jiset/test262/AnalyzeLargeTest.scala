package kr.ac.kaist.jiset.test262

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.js.Test262
import kr.ac.kaist.jiset.js.ast.Script
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util.JvmUseful._
import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.analyzer.domain._

class AnalyzeLargeTest extends Test262Test {
  val name: String = "test262AnalyzeTest"

  // logging
  LOG = true

  // test test262 test
  def doTest(script: Script, name: String): Unit =
    analyzeTest(script)

  // registration
  def init: Unit = check(name, { test262Test("analyze") })
  init
}
