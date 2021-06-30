package kr.ac.kaist.jiset.test262

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.phase.FilterMeta
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.util.Useful._

class ParseLargeTest extends Test262Test {
  val name: String = "test262ParseTest"

  // registration
  val config = FilterMeta.test262configSummary

  val PARSE_TIMEOUT = 100 // second

  // registration
  def init: Unit = check(name, {
    val targets = config.normal.map(_.name)
    val nf = getPrintWriter("./test262-parse-failed.log")
    var failed = 0
    ProgressBar("test262 parse test", targets).foreach(name => {
      val jsName = s"$TEST262_TEST_DIR/$name"
      getError {
        timeout(parseTest(parseFile(jsName)), PARSE_TIMEOUT)
      }.foreach(e => {
        failed += 1
        nf.println(s"$name: ${e.getMessage}")
        nf.flush()
      })
    })
    if (failed > 0) fail(s"$failed tests are failed to be parsed")
    nf.close()
  })
  init
}
