package kr.ac.kaist.jiset

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.util.Useful._
import org.scalatest._

class GrammarTest extends JISETTest {
  // tag name
  override val tag = "grammar"

  // targets
  val targets = List(
    "es2016", "es2017", "es2018", "es2019", "es2020", "recent"
  )

  // registration
  def init: Unit = {
    for (target <- targets) check(tag, target, {
      val version = getVersion(target)
      val filename = s"$GRAMMAR_DIR/$target.grammar"
      val answer = readFile(filename)
      val result = ECMAScript.parseGrammar(version).toString
      assert(answer == result)
    })
  }

  init
}
