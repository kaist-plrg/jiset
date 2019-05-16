package kr.ac.kaist.ase.spec

// ECMAScript grammars
case class Grammar(prods: List[Production])
case class Production(
  lhs: String,
  rhsList: List[Rhs],
  semantics: List[String]
)
case class Rhs(tokens: List[String])
