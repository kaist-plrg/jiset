package kr.ac.kaist.ase.core

// CORE Left-Hand-Sides
sealed trait Lhs extends CoreNode {
  def getRef: Ref = this match {
    case LhsRef(ref) => ref
    case LhsLet(id) => RefId(id)
  }
}
case class LhsRef(ref: Ref) extends Lhs
case class LhsLet(id: Id) extends Lhs
