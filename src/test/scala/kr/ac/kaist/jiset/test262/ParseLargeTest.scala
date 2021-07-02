package kr.ac.kaist.jiset.test262

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.phase.FilterMeta
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.util.Useful._

class ParseLargeTest extends Test262Test {
  val name: String = "test262ParseTest"

  // registration
  val config = FilterMeta.test262configSummary

  // parser timeout
  val PARSE_TIMEOUT = 100 // second

  // registration
  def init: Unit = check(name, {
    mkdir(logDir)
    val targets = config.normal.map(_.name)
    var success = 0
    val snf = getPrintWriter(s"$logDir/test262-parse-success.log")
    var failed = 0
    val nf = getPrintWriter(s"$logDir/test262-parse-failed.log")
    val progress = ProgressBar("test262 parse test", targets)
    progress.postfix = () => s" - F/P = $failed/$success"
    for (name <- progress) {
      val jsName = s"$TEST262_TEST_DIR/$name"
      getError {
        timeout(parseTest(parseFile(jsName)), PARSE_TIMEOUT)
        success += 1
        snf.println(name)
        snf.flush()
      }.foreach(e => {
        failed += 1
        nf.println(s"$name: ${e.getMessage}")
        nf.flush()
      })
    }
    if (failed > 0) fail(s"$failed tests are failed to be parsed")
    snf.close()
    nf.close()
  })
  init
}
