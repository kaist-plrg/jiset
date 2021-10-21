package kr.ac.kaist.jiset.test262

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.js.ast.Script
import kr.ac.kaist.jiset.phase.FilterMeta
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util.JvmUseful._

class ESParseLargeTest extends Test262Test {
  val name: String = "test262ESParseTest"

  // parser timeout
  val PARSE_TIMEOUT = 100 // second

  // test test262 test
  def doTest(script: Script, name: String): Unit =
    esparseTest(script, s"$TEST262_TEST_DIR/$name")

  // parse
  override def parse(filename: String, includes: List[String]): Script =
    parseFile(filename)

  // registration
  def init: Unit = check(name, { test262Test("esparse") })
  init
}
