package kr.ac.kaist.jiset.grammar

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.parser.ECMAScriptParser
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.util.Useful._
import org.scalatest._

class BasicSmallTest extends GrammarTest {
  // registration
  def init: Unit = {
    for (version <- VERSIONS) check(version, {
      val filename = s"$GRAMMAR_DIR/$version.grammar"
      val answer = readFile(filename)
      implicit val (lines, document) = getInput(version)
      val grammar = ECMAScriptParser.parseGrammar
      assert(answer == grammar.toString)
    })
  }
  init
}
