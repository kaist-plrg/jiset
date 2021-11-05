package kr.ac.kaist.jiset.editor

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.js.ast._
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
        a = mergeStmtList(removed, ast.parserParams).get
        transformed = true
      }
    }
    a
  }

  val name: String = "random2"
  val retryMax = 10
  def _mutate: Option[Script] = {
    transformed = false
    val removed = transform(program.script)
    if (transformed) Some(removed) else None
  }
}

// random3 mutator
case class RandomMutator3(program: JsProgram, nids: Set[Int]) extends Mutator with ASTTransformer {
  // flag
  var transformed: Boolean = false

  // rewrite ast to new expr if given expr is function call
  private def callRewriter(ast: LeftHandSideExpression): LeftHandSideExpression = {
    // parse str to NewExpresssion
    def parse(str: String, params: List[Boolean]): NewExpression =
      Parser.parse(Parser.NewExpression(params), str).get

    def isAsync(ccExpr: CoverCallExpressionAndAsyncArrowHead0): Boolean =
      ccExpr.x0.toString == "async"

    def rewrite(cExpr: CallExpression): Option[NewExpression] = {
      cExpr match {
        case CallExpression0(expr, _, _) => {
          val ccExpr = expr.asInstanceOf[CoverCallExpressionAndAsyncArrowHead0]
          val args = getArguments(ccExpr.x1)
          if (!isAsync(ccExpr) && !args.isEmpty) {
            val rewrites = args.map(arg => arg.toString)
            val str = s"(${rewrites.mkString(",")})"
            val nExpr = parse(str, ast.parserParams)
            Some(nExpr)
          } else None
        }
        case _ => None
      }
    }

    ast match {
      // call Expression
      case LeftHandSideExpression1(x0, ps, span) => {
        val nExprOpt = rewrite(x0)
        nExprOpt match {
          case Some(nExpr) => {
            transformed = true
            // new Expression
            LeftHandSideExpression0(nExpr, ps, span)
          }
          case None => ast
        }
      }
      case _ => ast
    }
  }

  // function transformation
  override def transform(ast: LeftHandSideExpression): LeftHandSideExpression = {
    var a = ast
    if (!transformed) {
      a = callRewriter(ast)
    }
    a
  }

  // parenthesized expr transformation
  override def transform(
    ast: CoverParenthesizedExpressionAndArrowParameterList
  ): CoverParenthesizedExpressionAndArrowParameterList = {
    var a = ast
    if (!transformed) {
      val (exprs, ps, span) = flattenExpr(ast)
      val length = exprs.length
      if (length > 0) {
        val sep = randInt(length)
        val removed = exprs.take(sep) ++ exprs.takeRight(exprs.size - sep - 1)
        a = mergeExpr(removed, ps, span)
        transformed = true
      }
    }
    a
  }

  val name: String = "random3"
  val retryMax = 10
  def _mutate: Option[Script] = {
    transformed = false
    val removed = transform(program.script)
    if (transformed) Some(removed) else None
  }
}
