package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.util.Useful._

class BasicSmallTest extends IRTest {
  def test(path: String): Unit = {
    val (algos, inst) = IRParser.fileToIR(path)
    val sem = new Semantics(inst, algos)
    val transfer = new Transfer(sem, isDebug = false, silent = false, timeLimit = Some(3))
    transfer.compute
  }

  // registration
  def init: Unit = {
    for (file <- walkTree(IR_DIR)) {
      val filename = file.getName
      if (irFilter(filename))
        check(filename, test(file.toString))
    }
  }
  init
}
