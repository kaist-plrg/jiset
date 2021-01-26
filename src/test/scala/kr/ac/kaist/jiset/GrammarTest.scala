package kr.ac.kaist.jiset

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.parser.ECMAScriptParser
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.util.Useful._
import org.scalatest._

class GrammarTest extends JISETTest {
  // tag name
  override def tag = "grammar"

  // registration
  override def executeTests: Unit = {
    for (version <- VERSIONS) check(tag, version, {
      val filename = s"$GRAMMAR_DIR/$version.grammar"
      val answer = readFile(filename)
      val (grammar, _) = ECMAScriptParser.parseGrammar(version)
      assert(answer == grammar.toString)
    })
  }
}
