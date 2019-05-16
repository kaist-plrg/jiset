package kr.ac.kaist.ase.spec

// ECMASCript specifications
case class Spec(
  globalMethods: List[String],
  grammar: Grammar,
  tys: List[Ty]
)
