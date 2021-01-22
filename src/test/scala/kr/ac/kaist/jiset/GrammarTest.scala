package kr.ac.kaist.jiset

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.parser.ECMAScriptParser
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.util.Useful._
import org.scalatest._

class GrammarTest extends JISETTest {
  // tag name
  override val tag = "grammar"

  // versions
  val versions = List(
    "es2016", "es2017", "es2018", "es2019", "es2020", "recent"
  )

  // registration
  def init: Unit = {
    for (version <- versions) check(tag, version, {
      val filename = s"$GRAMMAR_DIR/$version.grammar"
      val answer = readFile(filename)
      val (grammar, _) = ECMAScriptParser.parseGrammar(version)
      assert(answer == grammar.toString)
    })
  }

  init
}
