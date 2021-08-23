package kr.ac.kaist.jiset.test262

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.js.Test262
import kr.ac.kaist.jiset.util.Useful._

class AnalyzeLargeTest extends Test262Test {
  val name: String = "test262AnalyzeTest"

  // offset and stride
  val offset = optional(System.getenv("JSAVER_OFFSET").toInt).getOrElse(0)
  val stride = optional(System.getenv("JSAVER_STRIDE").toInt).getOrElse(1)

  // registration
  def init: Unit = check(name, {
    val sliced = slice(Test262.config.normal, offset, stride)
    test262Test(sliced, TestKind.Analyze)
  })
  init
}
