package kr.ac.kaist.jiset.extract

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.spec.grammar._
import kr.ac.kaist.jiset.util.JvmUseful._
import org.scalatest._

class GrammarSmallTest extends ExtractTest {
  val name: String = "extractGrammarTest"

  // registration
  def init: Unit = check(VERSION, {
    val filename = s"$GRAMMAR_DIR/$VERSION.grammar"
    val answer = readFile(filename)
    val grammar = JISETTest.spec.grammar
    assert(answer == grammar.beautified)
  })
  init
}
