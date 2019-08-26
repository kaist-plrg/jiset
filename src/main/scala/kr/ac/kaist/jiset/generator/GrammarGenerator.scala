package kr.ac.kaist.jiset.generator

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.spec._

object GrammarGenerator {
  def apply(grammar: Grammar): Unit = {
    ASTGenerator(grammar)
    ParserGenerator(grammar)
  }
}
