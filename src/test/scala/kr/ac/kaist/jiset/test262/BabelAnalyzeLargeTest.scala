package kr.ac.kaist.jiset.test262

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.js.Test262
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util.JvmUseful._

class BabelAnalyzeLargeTest extends Test262Test {
  val name: String = "test262BabelAnalyzeTest"

  // offset and stride
  val offset = optional(System.getenv("JSAVER_OFFSET").toInt).getOrElse(0)
  val stride = optional(System.getenv("JSAVER_STRIDE").toInt).getOrElse(1)

  // filename for analyze target names
  val filename = s"$BASE_DIR/tests/analyze-test262"

  // logging
  LOG = true

  // registration
  def init: Unit = check(name, {
    // compile Test262 programs using Babel
    println("install a wrapper of babel...")
    executeCmd("npm install", s"$BASE_DIR/lib/compiler")
    println("compile Test262 programs using Babel...")
    executeCmd("npm run start", s"$BASE_DIR/lib/compiler")

    // perform analyze tests with compiled programs
    val targets = readFile(filename).split(LINE_SEP).toSet
    val tests = Test262.config.normal.filter(targets contains _.name)
    val sliced = slice(tests, offset, stride)
    test262Test(sliced, TestKind.Analyze, s"$BASE_DIR/logs/analyze/compiled-test262")
  })
  init
}
