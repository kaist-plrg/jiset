package kr.ac.kaist.jiset.ir

// IR References
sealed trait Ref extends IRNode {
  def base: String = this match {
    case RefId(Id(name)) => name
    case RefProp(ref, _) => ref.base
  }
}
case class RefId(id: Id) extends Ref
case class RefProp(ref: Ref, expr: Expr) extends Ref
