package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset._

class BasicSmallTest extends IRTest {
  def test(path: String): Unit = {
    val interp = new Interp(isDebug = false, silent = true, timeLimit = Some(3))
    val insts = Parser.fileToInsts(path)
    val emptyState = State(Env(), Heap())
    interp(insts)(emptyState)
  }

  // TODO remove targets
  val targets = Set(
    "assert.ir",
    "expr-bool.ir",
    "expr-null.ir",
    "expr-undef.ir",
    "print1.ir",
    "let1.ir",
    "assign1.ir",
    "list1.ir",
    "list2.ir",
    "list3.ir",
    "list4.ir",
    "map1.ir",
    "map2.ir",
    "prop-delete1.ir",
    "prop-delete2.ir",
    "delete1.ir"
  )

  // registration
  def init: Unit = {
    for (file <- walkTree(IR_DIR)) {
      val filename = file.getName
      if (irFilter(filename) && targets.contains(filename))
        check(filename, test(file.toString))
    }
  }
  init
}
