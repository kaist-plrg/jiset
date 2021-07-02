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

  // targets
  val targets = config.normal.map(_.name)

  // progress bar
  val progress = ProgressBar("test262 parse test", targets)

  // summary
  val summary = progress.summary

  // registration
  def init: Unit = check(name, {
    mkdir(logDir)
    for (name <- progress) {
      val jsName = s"$TEST262_TEST_DIR/$name"
      getError {
        timeout(parseTest(parseFile(jsName)), PARSE_TIMEOUT)
        summary.passes :+= name
      }.foreach(e => {
        summary.fails :+= name
      })
    }
    dumpFile(summary.fails.mkString(LINE_SEP), s"$logDir/parse-fail.log")
    dumpFile(summary.passes.mkString(LINE_SEP), s"$logDir/parse-pass.log")
    dumpFile(summary, s"$logDir/parse-summary")
    if (summary.fail > 0) fail(s"${summary.fail} tests are failed.")
  })
  init
}
