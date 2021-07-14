package kr.ac.kaist.jiset.test262

import kr.ac.kaist.jiset.js.Test262

class EvalLargeTest extends Test262Test {
  val name: String = "test262EvalTest"

  // registration
  def init: Unit = check(name, {
    test262EvalTest(Test262.config.normal, "eval")
  })
  init
}
