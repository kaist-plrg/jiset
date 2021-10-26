package kr.ac.kaist.jiset.js

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.error.NotSupported
import kr.ac.kaist.jiset.js.ast._
import kr.ac.kaist.jiset.phase.FilterMeta
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util.JvmUseful._

object Test262 {
  // parsing result
  type ParseResult = Either[String, List[StatementListItem]]
  type LAParser[T] = Parser.LAParser[T]

  // test262 test configuration
  lazy val config = FilterMeta.test262configSummary

  // parse JavaScript file
  def parseFile(filename: String): Script =
    Parser.parse(Parser.Script(Nil), fileReader(filename)).get

  // cache for parsing results for necessary harness files
  val getInclude = cached[String, ParseResult](name => try {
    val filename = s"$TEST262_DIR/harness/$name"
    val script = parseFile(filename)
    Right(flattenStmt(script))
  } catch {
    case NotSupported(msg) => Left(msg)
  })

  // basic statements
  lazy val basicStmts = for {
    x <- getInclude("assert.js")
    y <- getInclude("sta.js")
  } yield x ++ y

  // Assertion Remover
  class AssertionRemover extends ASTTransformer {
    import AssertionRemover._

    // handle assertion calls
    override def transform(ast: Statement): Statement = {
      // check if rewrite is needed
      val checker = new RewriteChecker(ast)
      checker.walk(ast)

      // rewrite statement
      if (checker.needRewrite) {
        // get assert call and rewrite assertion
        val assertCall = ast.getElems("CoverCallExpressionAndAsyncArrowHead")(0)
        val stmts = rewriteAssertion(assertCall.asInstanceOf[CoverCallExpressionAndAsyncArrowHead])

        // transform to Statement
        if (stmts.isEmpty) getEmptyStmt(ast.parserParams)
        else super.transform(fromStmts(stmts, ast.parserParams))
      } else if (checker.nested) super.transform(ast)
      else ast
    }

    // handle new `Test262Error` => new function() {}
    override def transform(ast: MemberExpression): MemberExpression =
      ast match {
        case expr @ MemberExpression6(x1, x2, params, span) if x1.toString == TEST262_ERROR =>
          val emptyFunc = getEmptyFunc(Parser.MemberExpression(x1.parserParams))
          MemberExpression6(emptyFunc, super.transform(x2), params, span)
        case _ => super.transform(ast)
      }

    // handle e instanceof `Test262Error` => e instanceof function () {}
    override def transform(ast: RelationalExpression): RelationalExpression =
      ast match {
        case expr @ RelationalExpression5(x0, x2, params, span) if x2.toString == TEST262_ERROR =>
          val emptyFunc = getEmptyFunc(Parser.ShiftExpression(x2.parserParams))
          RelationalExpression5(super.transform(x0), emptyFunc, params, span)
        case _ => super.transform(ast)
      }

    // helpers
    def getEmptyFunc[T](p: LAParser[T]): T =
      Parser.parse(p, "function() {}").get
    def getEmptyStmt(ps: List[Boolean]): Statement =
      Parser.parse(Parser.Statement(ps), "{}").get
    def fromStmts(
      stmts: List[Statement],
      ps: List[Boolean]
    ): Statement = {
      val stmtItems = stmts.map(s => StatementListItem0(s, s.parserParams, Span()))
      val stmtList = mergeStmtList(stmtItems, ps)
      val block = Block0(stmtList, ps, Span())
      val blockStmt = BlockStatement0(block, ps, Span())
      Statement0(blockStmt, ps, Span())
    }
  }
  object AssertionRemover {
    // remove assertion
    def apply(script: Script): Script = {
      val removed = (new AssertionRemover).transform(script)
      (new FlattenTransformer).transform(removed)
    }

    // harness related to assertion
    lazy val harness = Set(
      "deepEqual.js",
      "compareArray.js",
      "compareIterator.js",
      "assertRelativeDateMs.js",
      "propertyHelper.js",
      "promiseHelper.js"
    )

    // # of arguments to preserve for assertion removal
    lazy val argsMap = Map(
      //assert.js
      "assert" -> 1,
      "assert . sameValue" -> 1,
      "assert . notSameValue" -> 1,

      //deepEqual.js
      "assert . deepEqual" -> 1,

      //compareArray.js
      "compareArray" -> 2,
      "compareArray . isSameValue" -> 2,
      "compareArray . format" -> 1,
      "assert . compareArray" -> 1,

      //compareIterator.js
      "assert . compareIterator" -> 1,

      //assertRelativeDateMs.js
      "assertRelativeDateMs" -> 1,

      //sta.js
      "$ERROR" -> 0,
      "$DONOTEVALUATE" -> 0,

      //propertyHelper.js
      "verifyProperty" -> 3,
      "verifyEqualTo" -> 2,
      "verifyWritable" -> 3,
      "verifyNotWritable" -> 3,
      "verifyEnumerable" -> 2,
      "verifyNotEnumerable" -> 2,
      "verifyConfigurable" -> 2,
      "verifyNotConfigurable" -> 2,

      //promiseHelper.js
      "checkSequence" -> 1,
      "checkSettledPromises" -> 1,
    )
    val ASSERT_THROW = "assert . throws"
    val TEST262_ERROR = "Test262Error"

    // check if statement rewrite is required
    class RewriteChecker(target: Statement) extends ASTWalker {
      var (hasAssertion, nested) = (false, false)
      def needRewrite: Boolean = hasAssertion && !nested

      // check nested statement
      override def walk(ast: Statement) = {
        if (ast == target) super.walk(ast)
        else nested = true
      }

      // check assertion function calls
      override def walk(ast: CoverCallExpressionAndAsyncArrowHead) = ast match {
        case CoverCallExpressionAndAsyncArrowHead0(x0, _, _, _) =>
          val funcName = x0.toString
          val isAssertion = argsMap.contains(funcName) || funcName == ASSERT_THROW
          if (isAssertion) hasAssertion = true
      }
    }

    // flatten nested block statements
    class FlattenTransformer extends ASTTransformer {
      private def aux(ast: Option[StatementList]): Option[StatementList] =
        ast.flatMap { sl =>
          val stmts = flattenStmtList(sl).flatMap(getItems)
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
        val stmts = flattenStmt(ast).flatMap(getItems)
        super.transform(mergeStmt(stmts))
      }

      // helpers
      def getItems(ast: StatementListItem): List[StatementListItem] = ast match {
        case item @ StatementListItem0(stmt, _, _) => getItems(stmt, List(item))
        case decl => List(decl)
      }
      def getItems(
        ast: Statement,
        default: List[StatementListItem]
      ): List[StatementListItem] = ast match {
        case Statement0(x0, _, _) => getItems(x0, default)
        case _ => default
      }
      def getItems(
        ast: BlockStatement,
        default: List[StatementListItem]
      ): List[StatementListItem] = ast match {
        case BlockStatement0(x0, _, _) => getItems(x0, default)
        case _ => default
      }
      def getItems(
        ast: Block,
        default: List[StatementListItem]
      ): List[StatementListItem] = ast match {
        case Block0(Some(sl), _, _) => flattenStmtList(sl).flatMap(getItems)
        case Block0(None, _, _) => List()
        case _ => default
      }
    }

    // rewrite assertion in CoverCallExpressionAndAsyncArrowHead
    def rewriteAssertion(assertCall: CoverCallExpressionAndAsyncArrowHead): List[Statement] = {
      // get argument list from Arguments
      def getArguments(ast: Arguments): List[AssignmentExpression] = ast match {
        case Arguments0(_, _) => List()
        case Arguments1(x1, _, _) => _getArguments(x1)
        case Arguments2(x1, _, _) => _getArguments(x1)
      }
      def _getArguments(ast: ArgumentList): List[AssignmentExpression] = ast match {
        case ArgumentList0(x0, _, _) => List(x0)
        case ArgumentList1(x0, _, _) => List(x0)
        case ArgumentList2(x0, x2, _, _) => _getArguments(x0) ++ List(x2)
        case ArgumentList3(x0, x2, _, _) => _getArguments(x0) ++ List(x2)
      }

      // parse str to Statement
      def parse(str: String, params: List[Boolean]): Statement =
        Parser.parse(Parser.Statement(params), str).get

      // fix parser params from AssignmentExpression to Statement
      def fixParams(ps: List[Boolean]): List[Boolean] = {
        val List(_, pYield, pAwait) = ps
        List(pYield, pAwait, false) // TODO correct?
      }

      // get string of rewrite statements
      val rewrites = assertCall match {
        case CoverCallExpressionAndAsyncArrowHead0(x0, x1, _, _) =>
          argsMap.get(x0.toString) match {
            case Some(argc) =>
              // if function name is in argsMap, take args by amount of argc
              getArguments(x1)
                .take(argc)
                .map(arg => {
                  val argStr = arg.toString
                  val str = if (argStr.startsWith("{")) s"($argStr);" else argStr
                  (str, fixParams(arg.parserParams))
                })
            case None if x0.toString == ASSERT_THROW =>
              // if throw assertion, wrap try-catch
              getArguments(x1)
                .lift(1)
                .map(cb => (
                  s"try { ($cb)(); } catch {}",
                  fixParams(cb.parserParams)
                ))
                .toList
            case _ => ??? // TODO error
          }
      }

      // re-parse rewrite strs to Statement
      rewrites.map {
        case (str, params) => parse(str, params)
      }
    }
  }

  // load test262 test file
  def loadTest262(
    script: Script,
    harness: List[String],
    noAssert: Boolean = false
  ): Script = {
    // choose basic assert harnesses
    val baseStmts = if (noAssert) Right(List()) else basicStmts

    // filter assertion harness
    val filtered =
      if (noAssert)
        harness.filter(!AssertionRemover.harness.contains(_))
      else
        harness

    // load harness
    val harnessStmts = filtered.foldLeft(baseStmts) {
      case (li, s) => for {
        x <- li
        y <- getInclude(s)
      } yield x ++ y
    } match {
      case Right(l) => l
      case Left(msg) => throw NotSupported(msg)
    }

    // remove assertion
    val removed =
      if (noAssert) timeout(AssertionRemover(script), TIMEOUT) // TODO debug
      else script

    // prepend harness to script
    mergeStmt(harnessStmts ++ flattenStmt(removed))
  }
}
