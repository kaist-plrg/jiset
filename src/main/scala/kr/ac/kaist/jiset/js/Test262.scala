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

    // helper for remove assertion
    def aux(l: Option[StatementList]): Option[StatementList] =
      l.flatMap { sl =>
        val stmts = flattenStmtList(sl).flatMap(removeNested)
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
      val stmts = flattenStmt(ast).flatMap(removeNested)
      super.transform(mergeStmt(stmts))
    }

    // create empty function
    private def getEmptyFunc(ps: List[Boolean]): ShiftExpression =
      Parser.parse(Parser.ShiftExpression(ps), "function () {}").get

    // get empty function
    def getEmptyFunc[T](p: LAParser[T]): T =
      Parser.parse(p, "function() {}").get

    // handle new `Test262Error` => new function() {}
    override def transform(ast: MemberExpression): MemberExpression =
      ast match {
        case expr @ MemberExpression6(x1, x2, params, span) if x1.toString == "Test262Error" =>
          val emptyFunc = getEmptyFunc(Parser.MemberExpression(x1.parserParams))
          MemberExpression6(emptyFunc, super.transform(x2), params, span)
        case _ => super.transform(ast)
      }

    // handle e instanceof `Test262Error` => e instanceof function () {}
    override def transform(ast: RelationalExpression): RelationalExpression =
      ast match {
        case expr @ RelationalExpression5(x0, x2, params, span) if x2.toString == "Test262Error" =>
          val emptyFunc = getEmptyFunc(Parser.ShiftExpression(x2.parserParams))
          RelationalExpression5(super.transform(x0), emptyFunc, params, span)
        case _ => super.transform(ast)
      }
  }
  object AssertionRemover {
    // remove assertion
    def apply(script: Script): Script = {
      (new AssertionRemover).transform(script)
    }

    // harness related to assertion
    lazy val assertions = Set(
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

    // helpers for removal
    implicit def downcast[T <: AST](ast: AST): T = ast.asInstanceOf[T]
    implicit def downcastOption[T <: AST](ast: Option[AST]): Option[T] =
      ast.map(downcast[T])

    // check if there is nested statement list item
    class NestedChecker(target: StatementListItem) extends ASTWalker {
      var nested = false
      override def job(ast: AST) = ast match {
        case stmt: StatementListItem if ast != target =>
          nested = true
        case _ =>
      }
      // guard for assertion function calls
      override def walk(ast: CoverCallExpressionAndAsyncArrowHead) = ast match {
        case CoverCallExpressionAndAsyncArrowHead0(x0, _, _, _) =>
          val funcName = x0.toString
          val guarded = argsMap.contains(funcName) || funcName == ASSERT_THROW
          if (!guarded) super.walk(ast)

      }
    }
    def checkNested(ast: StatementListItem): Boolean = {
      val checker = new NestedChecker(ast)
      checker.walk(ast)
      checker.nested
    }

    // remove assertion in nested statement list item
    def removeNested(ast: StatementListItem): List[StatementListItem] = {
      val nested = checkNested(ast)
      if (nested) List((new AssertionRemover).transform(ast))
      else removeAssertion(ast)
    }

    // remove assertion in deepest statement list item
    def removeAssertion(ast: StatementListItem): List[StatementListItem] = {
      // get first call
      def getFirstCall: Option[CoverCallExpressionAndAsyncArrowHead] =
        ast.getElems("CoverCallExpressionAndAsyncArrowHead").lift(0)

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

      // parse str to StatementListItem
      def parse(str: String, params: List[Boolean]): StatementListItem =
        try {
          Parser.parse(Parser.StatementListItem(params), str).get
        } catch {
          case err: Throwable =>
            println(ast.toString, ast.parserParams)
            println(str, params)
            throw err
        }

      // fix parser params from AssignmentExpression to StatementListItem
      def fixParams(ps: List[Boolean]): List[Boolean] = {
        val List(_, pYield, pAwait) = ps
        val List(_, _, pReturn) = ast.parserParams // TODO correct?
        List(pYield, pAwait, pReturn)
      }

      // get string of rewrite statements
      val rewrites = getFirstCall.flatMap {
        case CoverCallExpressionAndAsyncArrowHead0(x0, x1, _, _) =>
          argsMap.get(x0.toString) match {
            case Some(argc) =>
              // if function name is in argsMap, take args by amount of argc
              Some(
                getArguments(x1)
                  .take(argc)
                  .map(arg => (
                    s"(${arg.toString});",
                    fixParams(arg.parserParams)
                  ))
              )
            case None if x0.toString == ASSERT_THROW =>
              // if throw assertion, wrap try-catch
              Some(
                getArguments(x1)
                  .lift(1)
                  .map(cb => (
                    s"try { ($cb)(); } catch {}",
                    fixParams(cb.parserParams)
                  ))
                  .toList
              )
            case _ => None
          }
      }

      // re-parse rewrite strs to statement list item
      rewrites match {
        case Some(rs) => rs.flatMap {
          case (str, params) =>
            println(str)
            removeAssertion(parse(str, params))
        }
        case None => List(ast)
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
        harness.filter(!AssertionRemover.assertions.contains(_))
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
      if (noAssert) AssertionRemover(script)
      else script

    // prepend harness to script
    mergeStmt(harnessStmts ++ flattenStmt(removed))
  }
}
