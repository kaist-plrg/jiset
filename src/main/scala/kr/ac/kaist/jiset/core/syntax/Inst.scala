package kr.ac.kaist.jiset.core

// CORE Instructions
sealed trait Inst extends CoreNode
case class IExpr(expr: Expr) extends Inst
case class ILet(id: Id, expr: Expr) extends Inst
case class IAssign(ref: Ref, expr: Expr) extends Inst
case class IDelete(ref: Ref) extends Inst
case class IAppend(expr: Expr, list: Expr) extends Inst
case class IPrepend(expr: Expr, list: Expr) extends Inst
case class IReturn(expr: Expr) extends Inst
case class IIf(cond: Expr, thenInst: Inst, elseInst: Inst) extends Inst
case class IWhile(cond: Expr, body: Inst) extends Inst
case class ISeq(insts: List[Inst]) extends Inst
case class IAssert(expr: Expr) extends Inst
case class IPrint(expr: Expr) extends Inst
case class IApp(id: Id, fexpr: Expr, args: List[Expr]) extends Inst
case class IAccess(id: Id, bexpr: Expr, expr: Expr) extends Inst
case class IWithCont(id: Id, params: List[Id], bodyInst: Inst) extends Inst
