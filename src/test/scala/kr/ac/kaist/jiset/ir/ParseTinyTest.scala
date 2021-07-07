package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.JvmUseful._

class ParseTinyTest extends IRTest {
  val name: String = "irParseTest"

  // registration
  def init: Unit = for (file <- walkTree(IR_DIR)) {
    val filename = file.getName
    if (irFilter(filename)) check(filename, {
      val name = file.toString
      irParseTestFile(name)
    })
  }
  init
}
