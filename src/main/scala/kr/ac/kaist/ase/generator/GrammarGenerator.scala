package kr.ac.kaist.ase.generator

import kr.ac.kaist.ase._
import kr.ac.kaist.ase.util.Useful._
import kr.ac.kaist.ase.spec._

object GrammarGenerator {
  def apply(grammar: Grammar): Unit = {
    ASTGenerator(grammar)
  }
}
