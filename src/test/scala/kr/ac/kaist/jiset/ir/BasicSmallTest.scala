package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset._

class BasicSmallTest extends IRTest {
  def test(path: String): Unit = {
    val interp = new Interp(isDebug = true, timeLimit = Some(3))
    val insts = Parser.fileToInsts(path)
    val emptyState = State(Env(), Heap())
    interp(insts)(emptyState)
  }

  val pattern = "".r

  // registration
  def init: Unit = {
    for (file <- walkTree(IR_DIR)) {
      val filename = file.getName
      if (irFilter(filename) && pattern.matches(filename))
        check(filename, test(file.toString))
    }
  }
  init
}
