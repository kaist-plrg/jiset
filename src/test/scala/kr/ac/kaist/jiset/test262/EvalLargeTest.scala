package kr.ac.kaist.jiset.test262

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.js.Test262

class EvalLargeTest extends Test262Test {
  val name: String = "test262Test"

  // logging execution traces
  LOG = true

  // registration
  def init: Unit = check(name, {
    test262Test(Test262.config.normal, TestKind.Eval)
  })
  init
}
