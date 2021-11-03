package kr.ac.kaist.jiset.editor

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.js.ast.Script
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.util.JvmUseful._
import kr.ac.kaist.jiset.util.Useful._

// random mutator
case class RandomMutator(program: JsProgram, nids: Set[Int]) extends Mutator {
  val name: String = "random"
  val retryMax = 10
  def _mutate: Option[Script] = {
    val stmts = flattenStmt(program.script)
    val sep = randInt(stmts.length)
    val removed = stmts.take(sep) ++ stmts.takeRight(stmts.size - sep - 1)
    Some(mergeStmt(removed))
  }
}
