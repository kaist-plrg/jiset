package kr.ac.kaist.jiset.js

import kr.ac.kaist.jiset.JS_DIR
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.phase._
import kr.ac.kaist.jiset.util.Useful._

class EvalSmallTest extends JSTest {
  val name: String = "jsEvalTest"

  // registration
  def init: Unit = for (file <- walkTree(JS_DIR)) {
    val filename = file.getName
    if (jsFilter(filename)) check(filename, {
      val name = removedExt(filename)

      val jsName = file.toString
      val st = evalFile(jsName)

      val irName = js2ir(jsName)
      val program = irParseFile(irName)
      st.context.insts = program.insts
      Interp(st, irName)
    })
  }
  init
}
