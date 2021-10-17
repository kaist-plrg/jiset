package kr.ac.kaist.jiset.test262

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.js.Test262
import kr.ac.kaist.jiset.util.JvmUseful._

class VisitStatTest extends Test262Test {
  val name: String = "VisitStatTest"

  // logging
  LOG = true

  // registration
  def init: Unit = check(name, {
    val targets = List()
    test262Test(targets, TestKind.VisitStat)
  })
  init
}
