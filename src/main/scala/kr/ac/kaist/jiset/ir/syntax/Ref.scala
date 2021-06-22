package kr.ac.kaist.jiset.ir

// IR References
sealed trait Ref extends IRNode
object Ref extends Parser[Ref]
case class RefId(id: Id) extends Ref
case class RefProp(ref: Ref, expr: Expr) extends Ref
