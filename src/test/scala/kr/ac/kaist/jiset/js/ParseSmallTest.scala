package kr.ac.kaist.jiset.js

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.util.Useful._

class ParseSmallTest extends JSTest {
  val name: String = "jsParseTest"

  // registration
  def init: Unit = for (file <- walkTree(JS_DIR)) {
    val filename = file.getName
    if (jsFilter(filename)) check(filename, {
      val jsName = file.toString
      val ast = parseFile(jsName)
      parseTest(ast)
    })
  }
  init
}
