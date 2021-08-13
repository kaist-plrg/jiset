package kr.ac.kaist.jiset.test262

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.error.{ InterpTimeout, NotSupported }
import kr.ac.kaist.jiset.js.JSTest
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.js.Test262._
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util.JvmUseful._

trait Test262Test extends JSTest {
  override val category: String = "test262"

  // directory name
  val logDir = s"$LOG_DIR/test262_$dateStr"

  // eval tests
  def test262EvalTest(
    targets: List[NormalTestConfig],
    name: String
  ): Unit = if (!targets.isEmpty) {
    val progress = ProgressBar(s"test262 $name test", targets)
    val summary = progress.summary
    mkdir(logDir)
    dumpFile(JISETTest.spec.version, s"$logDir/ecma262-version")
    dumpFile(currentVersion(BASE_DIR), s"$logDir/jiset-version")
    summary.timeouts.setPath(s"$logDir/$name-timeout.log")
    summary.yets.setPath(s"$logDir/$name-yet.log")
    summary.fails.setPath(s"$logDir/$name-fail.log")
    summary.passes.setPath(s"$logDir/$name-pass.log")
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
        evalTest(mergeStmt(stmts), jsName)
        summary.passes += name
      }.foreach {
        case InterpTimeout => summary.timeouts += name
        case NotSupported(msg) => summary.yets += s"$name: $msg"
        case e => summary.fails += s"$name: ${e.getMessage}"
      }
    }
    summary.close

    // dump logs
    IRLogger.dumpTo(s"$logDir/$name-logger")
    dumpFile(summary, s"$logDir/$name-summary")
    if (summary.timeout > 0) println(s"${summary.timeout} tests are timeout.")
    if (summary.yet > 0) println(s"${summary.yet} tests are not yet supported.")
    if (summary.fail > 0) fail(s"${summary.fail} tests are failed.")
  }
}
