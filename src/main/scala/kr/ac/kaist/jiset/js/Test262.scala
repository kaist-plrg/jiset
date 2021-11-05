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

    // check if statement rewrite is required
    private class RewriteChecker(target: Statement) extends ASTWalker {
      var (hasAssertion, nested) = (false, false)
      def needRewrite: Boolean = hasAssertion && !nested

      // check nested statement
      override def walk(ast: Statement) = {
        if (ast == target) super.walk(ast)
        else nested = true
      }
      override def walk(ast: ExpressionBody) = {
        nested = true
      }

      // check assertion function calls
      override def walk(ast: CoverCallExpressionAndAsyncArrowHead) = ast match {
        case CoverCallExpressionAndAsyncArrowHead0(x0, x1, _, _) =>
          val funcName = x0.toString
          val isAssertion = argsMap.contains(funcName) || funcName == ASSERT_THROW
          if (isAssertion) hasAssertion = true
          else super.walk(ast)
      }
    }

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

    // rewrite assertion in CoverCallExpressionAndAsyncArrowHead
    private def rewriteAssertion(assertCall: CoverCallExpressionAndAsyncArrowHead): List[Statement] = {

      // parse str to Statement
      def parse(str: String, params: List[Boolean]): Statement =
        Parser.parse(Parser.Statement(params), str).get

      // fix parser params from AssignmentExpression to Statement
      def fixParams(ps: List[Boolean]): List[Boolean] = {
        val List(_, pYield, pAwait) = ps
        List(pYield, pAwait, false) // TODO correct?
      }

      // add parenthesis to rewrite str
      def fixExprStr(str: String): String = {
        val prefix = List("{", "function", "class", "async")
        if (prefix.exists(str.startsWith(_))) s"($str);"
        else str
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
                  (fixExprStr(arg.toString), fixParams(arg.parserParams))
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

    // helpers
    private def getEmptyStmt(ps: List[Boolean]): Statement =
      Parser.parse(Parser.Statement(ps), "{}").get
    private def fromStmts(
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

    // # of arguments to preserve for assertion removal
    lazy val argsMap = Map(
      //assert.js
      "assert" -> 1,
      "assert . sameValue" -> 2,
      "assert . notSameValue" -> 2,

      //deepEqual.js
      "assert . deepEqual" -> 2,

      //compareArray.js
      "compareArray" -> 2,
      "compareArray . isSameValue" -> 2,
      "compareArray . format" -> 1,
      "assert . compareArray" -> 1,

      //compareIterator.js
      "assert . compareIterator" -> 2,

      //assertRelativeDateMs.js
      "assertRelativeDateMs" -> 2,

      //sta.js
      "$ERROR" -> 0,
      "$DONOTEVALUATE" -> 0,

      //propertyHelper.js
      "verifyProperty" -> 3,
      "verifyEqualTo" -> 3,
      "verifyWritable" -> 4,
      "verifyNotWritable" -> 4,
      "verifyEnumerable" -> 2,
      "verifyNotEnumerable" -> 2,
      "verifyConfigurable" -> 2,
      "verifyNotConfigurable" -> 2,

      //promiseHelper.js
      "checkSequence" -> 1,
      "checkSettledPromises" -> 2,

      // doneprintHandle.js
      "$DONE" -> 0,

      //nativeFunctionMatcher.js
      "assertToStringOrNativeFunction" -> 2,
      "assertNativeFunction" -> 2,
    )
    val ASSERT_THROW = "assert . throws"

    // flatten nested block statements
    class FlattenTransformer extends ASTTransformer {
      // helper
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
  }

  // harness remover
  class HarnessRemover extends ASTTransformer {
    import HarnessRemover._

    // handle FUNCS => function () {}
    override def transform(ast: MemberExpression): MemberExpression = {
      val parser = Parser.MemberExpression
      ast match {
        case expr if FUNCS.contains(expr.toString) =>
          getEmptyFunc(parser(expr.parserParams))
        case _ => super.transform(ast)
      }
    }

    // helpers
    private def getEmptyFunc[T](p: LAParser[T]): T =
      Parser.parse(p, "(function() {})").get
  }
  object HarnessRemover {
    // remove harness
    def apply(script: Script): Script = {
      val s0 = AssertionRemover(script)
      (new HarnessRemover).transform(s0)
    }

    // constants
    val FUNCS = Set(
      "Test262Error",
      "$DONE",
      "Test262Error . thrower",
      "isEnumerable",
      "isConstructor",
      "assert . sameValue",
      "allowProxyTraps",
    )

    val UTILS = Set(
      "hidden-constructors.js"
    )
  }

  // load test262 test file
  def loadTest(
    script: Script,
    includes: List[String],
    harness: Boolean = true
  ): Script = {
    // base statments
    val baseStmts = if (harness) basicStmts else Right(List())

    // filter includes
    val filtered =
      if (harness) includes
      else includes.filter(HarnessRemover.UTILS.contains(_))

    // get harness-prepended ast
    val ast =
      if (filtered.isEmpty) script
      else {
        // include harness
        val harnessStmts = filtered.foldLeft(baseStmts) {
          case (li, s) => for {
            x <- li
            y <- getInclude(s)
          } yield x ++ y
        } match {
          case Right(l) => l
          case Left(msg) => throw NotSupported(msg)
        }

        // prepend harness to original script
        mergeStmt(harnessStmts ++ flattenStmt(script))
      }

    // remove harness
    if (harness) ast
    else timeout(HarnessRemover(ast), TIMEOUT)
  }
}
