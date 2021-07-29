package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.JvmUseful._

class EvalTinyTest extends IRTest {
  val name: String = "irEvalTest"

  // registration
  def init: Unit = for (file <- walkTree(IR_DIR)) {
    val filename = file.getName
    if (irFilter(filename)) check(filename, {
      val irName = file.toString
      irEvalFile(irName)
    })
  }
  init
}
