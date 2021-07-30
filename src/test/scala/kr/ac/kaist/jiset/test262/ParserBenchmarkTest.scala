package kr.ac.kaist.jiset.test262

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.phase.FilterMeta
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util.JvmUseful._

class ParserBenchmarkTest extends Test262Test {
  val name: String = "test262ParseBenchmarkTest"

  import Test262._

  // parser timeout
  //val PARSE_TIMEOUT = 100 // second

  // progress bar
  val progress = ProgressBar("test262 paser-benchmark test", config.normal)

  // summary
  val summary = progress.summary

  // registration
  def init: Unit = check(name, {
    mkdir(logDir)
    dumpFile(JISETTest.spec.version, s"$logDir/ecma262-version")
    dumpFile(currentVersion(BASE_DIR), s"$logDir/jiset-version")
    summary.fails.setPath(s"$logDir/parser-benchmark-fail.log")
    summary.passes.setPath(s"$logDir/parser-benchmark-pass.log")

    var totalJS = 0L
    var totalES = 0L

    for (config <- progress) {
      val name = config.name
      val jsName = s"$TEST262_TEST_DIR/$name"
      getError {
        // Load file
        var (timeLoad, file2str) = time(readFile(jsName))

        // JSParse
        var (timeJS, jsAST) = time(parse(file2str))

        // ESParse
        val shellStr = escapeJS(file2str)
        var (timeES, esAST) = time(esparse(shellStr))

        // parsed with both
        totalES += timeES
        totalJS += timeJS
        summary.passes += name + "," + timeLoad + "," + timeJS + "," + timeES
      }.foreach(e => {
        // println(e)
        summary.fails += name
      })
    }
    val result = s"Total ${summary.pass} tests are parsed: $totalES ms took with ESParse, $totalJS ms took with JSParse"
    println(result)
    summary.close
    dumpFile(result, s"$logDir/parse-benchmark-summary")
  })
  init
}
