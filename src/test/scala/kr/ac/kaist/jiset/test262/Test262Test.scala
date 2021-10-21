package kr.ac.kaist.jiset.test262

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.error.{ InterpTimeout, AnalysisTimeout, AnalysisImprecise, NotSupported }
import kr.ac.kaist.jiset.js.JSTest
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.js.Test262._
import kr.ac.kaist.jiset.js.ast.Script
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util.JvmUseful._

trait Test262Test extends JSTest {
  override val category: String = "test262"

  // directory name
  val logDir = s"$LOG_DIR/test262_$dateStr"
  mkdir(logDir)

  // test262 targets
  val targets: List[NormalTestConfig] = Test262.config.normal

  // test test262 test
  def doTest(script: Script, name: String): Unit

  // dump test262 test stats
  def dumpStats(logDir: String): Unit = ()

  // get AST for test262 test
  def parse(filename: String, includes: List[String]): Script =
    loadTest262(filename, includes)

  // test 262 tests
  def test262Test(desc: String): Unit = {
    val progress = ProgressBar(s"test262 $desc test", targets)
    val summary = progress.summary

    // dump versions
    dumpFile(JISETTest.spec.version, s"$logDir/ecma262-version")
    dumpFile(currentVersion(BASE_DIR), s"$logDir/jiset-version")

    // set base log directory for summary
    summary.setBase(logDir, desc)

    // iter test262 configs
    for (config <- progress) {
      val NormalTestConfig(name, includes) = config
      val jsName = s"$TEST262_TEST_DIR/$name"

      try {
        doTest(parse(jsName, includes), name)
        summary.passes += name
      } catch {
        case InterpTimeout | AnalysisTimeout =>
          summary.timeouts += name
        case NotSupported(msg) =>
          summary.yets += s"$name: $msg"
        case e: Throwable =>
          summary.fails += s"$name: ${e.getMessage}"
      }
    }
    summary.close

    // dump logs
    dumpFile(summary, s"$logDir/$name-summary")
    dumpStats(s"$logDir/$desc")

    // print stats
    if (summary.timeout > 0) println(s"${summary.timeout} tests are timeout.")
    if (summary.yet > 0) println(s"${summary.yet} tests are not yet supported.")
    if (summary.fail > 0) fail(s"${summary.fail} tests are failed.")
  }
}
