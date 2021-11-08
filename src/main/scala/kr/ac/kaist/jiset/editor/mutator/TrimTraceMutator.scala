package kr.ac.kaist.jiset.editor

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.ir.{ State, NodeCursor, Interp }
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.js.ast._
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.util.JvmUseful._
import kr.ac.kaist.jiset.util.Useful._

case class TrimTraceMutator(program: JsProgram, nids: Set[Int]) extends Mutator {
  val name = "trim"
  val retryMax = 1

  // collect statement list item
  class StatementListCollector extends ASTWalker {
    var stmts: Set[StatementListItem] = Set()
    override def job(ast: AST): Unit = ast match {
      case stmt: StatementListItem => stmts += stmt
      case _ =>
    }

    // handle reparsed asts
    override def walk(ast: CoverCallExpressionAndAsyncArrowHead) = ()
    override def walk(ast: CoverParenthesizedExpressionAndArrowParameterList) = ()
    override def walk(ast: AssignmentExpression) = ast match {
      case AssignmentExpression4(x0, x2, ps, span) => walk(x2)
      case _ => super.walk(ast)
    }

  }

  // remove statment list item
  class StatementListRemover(targets: Set[StatementListItem]) extends ASTTransformer {
    private def noRemove(stmt: StatementListItem): Boolean =
      !targets.contains(stmt)

    // helper
    private def aux(ast: Option[StatementList]): Option[StatementList] =
      ast.flatMap { sl =>
        val stmts = flattenStmtList(sl).filter(noRemove(_))
        mergeStmtList(stmts, sl.parserParams)
      }

    // handle statement list
    override def transform(ast: Block): Block =
      ast match {
        case Block0(l, p, s) =>
          super.transform(Block0(aux(l), p, s))
      }
    override def transform(ast: CaseClause): CaseClause =
      ast match {
        case CaseClause0(e, l, p, s) =>
          super.transform(CaseClause0(e, aux(l), p, s))
      }
    override def transform(ast: DefaultClause): DefaultClause =
      ast match {
        case DefaultClause0(l, p, s) =>
          super.transform(DefaultClause0(aux(l), p, s))
      }
    override def transform(ast: FunctionStatementList): FunctionStatementList =
      ast match {
        case FunctionStatementList0(l, p, s) =>
          super.transform(FunctionStatementList0(aux(l), p, s))
      }
    override def transform(ast: Script): Script = {
      val stmts = flattenStmt(ast).filter(noRemove(_))
      super.transform(mergeStmt(stmts))
    }
  }

  // get current statement list of ast
  @annotation.tailrec
  private def getCurrentStmt(ast: AST): Option[StatementListItem] = ast match {
    case stmt: StatementListItem => Some(stmt)
    case _ => ast.parent match {
      case Some(parent) => getCurrentStmt(parent)
      case None => None
    }
  }

  def _mutate: Option[Script] = {
    val script = program.script

    // create mapping from statement list item
    val collector = new StatementListCollector
    collector.walk(script)

    // set targets to trim
    var targets = collector.stmts
    var notTouched = nids

    // run interp and dump touched result
    val initState = Initialize(script, None, NodeCursor)
    val interp = new Interp(initState, useHook = true)

    // subscribe step event in interp
    interp.subscribe(Interp.Event.Step, { st =>
      for {
        nid <- st.currentNode.map(_.uid)
      } {
        if (!notTouched.isEmpty) {
          // exclude current statement from targets
          for {
            ast <- st.currentAst
            currentStmt <- getCurrentStmt(ast)
          } { targets -= currentStmt }

          // remove current node from not touched set
          notTouched -= nid
        }
      }
    })

    // fixpoint
    interp.fixpoint

    // trim targets
    Some((new StatementListRemover(targets)).transform(script))
  }
}
