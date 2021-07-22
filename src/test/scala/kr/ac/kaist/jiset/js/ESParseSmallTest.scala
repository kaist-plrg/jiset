package kr.ac.kaist.jiset.js

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.util.JvmUseful._

class ESParseSmallTest extends JSTest {
  val name: String = "jsESParseTest"

  // registration
  def init: Unit = {

    cleanDir(AST_DIR)
    mkdir(AST_DIR)

    for (file <- walkTree(JS_DIR)) {
      val filename = file.getName
      if (jsFilter(filename)) check(filename, {
        val jsName = file.toString
        esparseTest(jsName, filename)
      })
    }
  }
  init
}
