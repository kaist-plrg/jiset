package kr.ac.kaist.jiset.editor

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.js.ast.{ Script, StatementList }
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.util.JvmUseful._
import kr.ac.kaist.jiset.util.Useful._

// random mutator
case class RandomMutator(program: JsProgram, nids: Set[Int]) extends Mutator with ASTTransformer {
  // handle stmts
  var transformed :Boolean = false
  override def transform(ast: StatementList): StatementList = if (!transformed) {
    val stmts = flattenStmtList(ast)
    val length = stmts.length
    if (length > 1) {
      val sep = randInt(length)
      val removed = stmts.take(sep) ++ stmts.takeRight(stmts.size - sep - 1)
      transformed = true
      mergeStmtList(removed, ast.parserParams).get
    } else ast
  } else ast

  val name: String = "random"
  val retryMax = 10
  def _mutate: Option[Script] = {
   val removed = transform(program.script)
   Some(removed)
  }
}
