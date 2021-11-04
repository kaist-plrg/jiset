package kr.ac.kaist.jiset.editor

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.js.ast.{ Script, StatementList, CoverCallExpressionAndAsyncArrowHead }
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.util.JvmUseful._
import kr.ac.kaist.jiset.util.Useful._

// random1 mutator
case class RandomMutator1(program: JsProgram, nids: Set[Int]) extends Mutator with ASTTransformer {
  // Toplevel transformation
  val name: String = "random1"
  val retryMax = 10
  def _mutate: Option[Script] = {
    val stmts = flattenStmt(program.script)
    val sep = randInt(stmts.length)
    val removed = stmts.take(sep) ++ stmts.takeRight(stmts.size - sep - 1)
    Some(mergeStmt(removed))
  }
}

// random2 mutator
case class RandomMutator2(program: JsProgram, nids: Set[Int]) extends Mutator with ASTTransformer {
  // flag
  var transformed: Boolean = false

  // In-depth transformation
  override def transform(ast: StatementList): StatementList = {
    var a = ast
    if (!transformed) {
      val stmts = flattenStmtList(ast)
      val length = stmts.length
      if (length > 1) {
        val sep = randInt(length)
        val removed = stmts.take(sep) ++ stmts.takeRight(stmts.size - sep - 1)
        transformed = true
        a = mergeStmtList(removed, ast.parserParams).get
      }
    }
    a
  }

  val name: String = "random2"
  val retryMax = 10
  def _mutate: Option[Script] = {
    transformed = false
    val removed = transform(program.script)
    Some(removed)
  }
}

// random3 mutator
case class Random2Mutator3(program: JsProgram, nids: Set[Int]) extends Mutator with ASTTransformer {
  // flag
  var transformed: Boolean = false

  // function transformation
  override def transform(ast: CoverCallExpressionAndAsyncArrowHead) = ???

  val name: String = "random3"
  val retryMax = 10
  def _mutate: Option[Script] = {
    val removed = transform(program.script)
    Some(removed)
  }
}
