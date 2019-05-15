package kr.ac.kaist.ase.core

// CORE References
sealed trait Ref extends CoreNode
case class RefId(id: Id) extends Ref
case class RefIdProp(ref: Ref, id: Id) extends Ref
case class RefStrProp(ref: Ref, expr: Expr) extends Ref
