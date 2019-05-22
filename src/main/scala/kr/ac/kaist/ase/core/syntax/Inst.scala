package kr.ac.kaist.ase.core

// CORE Instructions
sealed trait Inst extends CoreNode
case class IExpr(lhs: Lhs, expr: Expr) extends Inst
case class IDelete(ref: Ref) extends Inst
case class IReturn(expr: Expr) extends Inst
case class IIf(cond: Expr, thenInst: Inst, elseInst: Inst) extends Inst
case class IWhile(cond: Expr, body: Inst) extends Inst
case class ITry(lhs: Lhs, tryInst: Inst) extends Inst
case class IThrow(expr: Expr) extends Inst
case class ISeq(insts: List[Inst]) extends Inst
case class IAssert(expr: Expr) extends Inst
case class IPrint(expr: Expr) extends Inst
case class INotYetImpl(msg: String) extends Inst {
  override def toString: String = new StringContext("INotYetImpl(\"", "\")").s(msg)
}
