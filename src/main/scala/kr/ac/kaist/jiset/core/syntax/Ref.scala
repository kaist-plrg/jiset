package kr.ac.kaist.jiset.core

// CORE References
sealed trait Ref extends CoreNode
case class RefId(id: Id) extends Ref
case class RefProp(ref: Ref, expr: Expr) extends Ref
