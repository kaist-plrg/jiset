package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.ANALYZER_DIR
import kr.ac.kaist.jiset.JISETTest
import kr.ac.kaist.jiset.util.Useful._

class StringifyTinyTest extends AnalyzerTest {
  val name: String = "analyzerStringifyTest"

  // registration
  def init: Unit = check("stringify", {
    val sem = JISETTest.analysisResult
    val result = sem.toString
    val answer = readFile(s"$ANALYZER_DIR/stringify")
    assert(result == answer)
  })
  init
}
