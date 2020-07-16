package kr.ac.kaist.jiset.generator

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.spec._

object GrammarGenerator {
  def apply(packageName: String, modelDir: String, grammar: Grammar): Unit = {
    ASTGenerator(packageName, modelDir, grammar)
    ParserGenerator(packageName, modelDir, grammar)
  }
}
