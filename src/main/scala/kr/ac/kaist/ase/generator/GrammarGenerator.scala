package kr.ac.kaist.ase.generator

import kr.ac.kaist.ase._
import kr.ac.kaist.ase.util.Useful._
import kr.ac.kaist.ase.spec._

object GrammarGenerator {
  def apply(version: String, grammar: Grammar): Unit = {
    ASTGenerator(grammar)
    for (
      prod <- grammar.prods;
      (rhs, i) <- prod.rhsList.zipWithIndex;
      name <- rhs.semantics
    ) MethodGenerator(version, s"${prod.lhs.name}$i.$name")
    ASTParserGenerator(grammar)
  }
}
