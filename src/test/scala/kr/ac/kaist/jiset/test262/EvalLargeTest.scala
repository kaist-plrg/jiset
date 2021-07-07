package kr.ac.kaist.jiset.test262

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.error.NotSupported
import kr.ac.kaist.jiset.phase.FilterMeta
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util.JvmUseful._

class EvalLargeTest extends Test262Test {
  val name: String = "test262EvalTest"

  import Test262._

  // progress bar
  val progress = ProgressBar("test262 eval test", config.normal)

  // summary
  val summary = progress.summary

  // registration
  def init: Unit = check(name, {
    mkdir(logDir)
    summary.yets.setPath(s"$logDir/eval-yet.log")
    summary.fails.setPath(s"$logDir/eval-fail.log")
    summary.passes.setPath(s"$logDir/eval-pass.log")
    for (config <- progress) {
      val NormalTestConfig(name, includes) = config
      val jsName = s"$TEST262_TEST_DIR/$name"
      getError {
        val includeStmts = includes.foldLeft(basicStmts) {
          case (li, s) => for {
            x <- li
            y <- getInclude(s)
          } yield x ++ y
        } match {
          case Right(l) => l
          case Left(msg) => throw NotSupported(msg)
        }
        val stmts = includeStmts ++ flattenStmt(parseFile(jsName))
        evalTest(mergeStmt(stmts))
        summary.passes += name
      }.foreach {
        case NotSupported(msg) => summary.yets += s"$name: $msg"
        case e => summary.fails += s"$name: ${e.getMessage}"
      }
    }
    summary.close
    dumpFile(summary, s"$logDir/eval-summary")
    if (summary.yet > 0) println(s"${summary.yet} tests are not yet supported.")
    if (summary.fail > 0) fail(s"${summary.fail} tests are failed.")
  })
  init
}
