package kr.ac.kaist.jiset.test262

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.error.NotSupported
import kr.ac.kaist.jiset.phase.FilterMeta
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.js.ast.StatementListItem
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.util.Useful._

class EvalLargeTest extends Test262Test {
  val name: String = "test262EvalTest"

  // tests for js evaluation
  def evalJSTest(st: State): Unit = {
    st.context.locals.get(st.context.retId) match {
      case Some(addr: Addr) => st.heap(addr, Str("Type")) match {
        case (addr: Addr) =>
          assert(addr == st.globals.getOrElse(Id("CONST_normal"), Absent))
          st.heap(NamedAddr("REALM"), Str("printStr")) match {
            case Str(v) if (v contains "AsyncTestFailure") =>
              fail(s"print test failure: $v")
            case _ => ()
          }
        case v => fail(s"invalid completion type: $v")
      }
      case Some(v) => fail(s"return not an address: $v")
      case None => fail("no return value")
    }
  }

  // parsing result
  type ParseResult = Either[String, List[StatementListItem]]

  // test262 test configuration
  val config = FilterMeta.test262configSummary

  // necessary harness files for normal tests
  val includes = Set("assert.js", "sta.js") ++ (for {
    NormalTestConfig(_, includes) <- config.normal
    name <- includes
  } yield name)

  // cache for parsing results for necessary harness files
  val includeMap: Map[String, ParseResult] = (for (name <- includes.toList) yield name -> (try {
    val filename = s"$TEST262_DIR/harness/$name"
    val script = parseFile(filename)
    Right(flattenStmt(script))
  } catch {
    case NotSupported(msg) => Left(msg)
  })).toMap

  // basic statements
  val basicStmts = for {
    x <- includeMap("assert.js")
    y <- includeMap("sta.js")
  } yield x ++ y

  // registration
  def init: Unit = check(name, {
    mkdir(logDir)
    var notyet = 0
    val ynf = getPrintWriter(s"$logDir/test262-eval-notyet.log")
    var failed = 0
    val nf = getPrintWriter(s"$logDir/test262-eval-failed.log")
    val progress = ProgressBar("test262 eval test", config.normal)
    for (config <- progress) {
      val NormalTestConfig(name, includes) = config
      val jsName = s"$TEST262_TEST_DIR/$name"
      getError {
        val includeStmts = includes.foldLeft(basicStmts) {
          case (li, s) => for {
            x <- li
            y <- includeMap(s)
          } yield x ++ y
        } match {
          case Right(l) => l
          case Left(msg) => throw NotSupported(msg)
        }
        val stmts = includeStmts ++ flattenStmt(parseFile(jsName))
        evalTest(mergeStmt(stmts))
      }.foreach {
        case NotSupported(msg) => {
          notyet += 1
          ynf.println(s"$name: $msg")
          ynf.flush()
        }
        case e => {
          failed += 1
          nf.println(s"$name: ${e.getMessage}")
          nf.flush()
        }
      }
    }
    if (notyet > 0) println(s"$notyet tests are not yet supported.")
    if (failed > 0) fail(s"$failed tests are failed.")
    ynf.close()
    nf.close()
  })
  init
}
