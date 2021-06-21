package kr.ac.kaist.jiset.grammar

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.extractor.ECMAScriptParser
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.util.Useful._
import org.scalatest._

class BasicSmallTest extends GrammarTest {
  val name: String = "grammarBasicTest"

  // registration
  def init: Unit = check(VERSION, {
    val filename = s"$GRAMMAR_DIR/$VERSION.grammar"
    val answer = readFile(filename)
    val grammar = JISETTest.spec.grammar
    assert(answer == grammar.toString)
  })
  init
}
