package kr.ac.kaist.jiset.ir

// IR Instructions
sealed trait Inst extends IRNode { var line: Int = -1 }

// conditional instructions
sealed trait CondInst extends Inst { val cond: Expr }
case class IIf(cond: Expr, thenInst: Inst, elseInst: Inst) extends CondInst
case class IWhile(cond: Expr, body: Inst) extends CondInst

// call instructions
sealed trait CallInst extends Inst { val id: Id }
case class IApp(id: Id, fexpr: Expr, args: List[Expr]) extends CallInst
case class IAccess(id: Id, bexpr: Expr, expr: Expr) extends CallInst

// normal instructions
sealed trait NormalInst extends Inst
case class IExpr(expr: Expr) extends NormalInst
case class ILet(id: Id, expr: Expr) extends NormalInst
case class IAssign(ref: Ref, expr: Expr) extends NormalInst
case class IDelete(ref: Ref) extends NormalInst
case class IAppend(expr: Expr, list: Expr) extends NormalInst
case class IPrepend(expr: Expr, list: Expr) extends NormalInst
case class IReturn(expr: Expr) extends NormalInst
case class IThrow(id: Id) extends NormalInst
case class IAssert(expr: Expr) extends NormalInst
case class IPrint(expr: Expr) extends NormalInst
case class IWithCont(id: Id, params: List[Id], bodyInst: Inst) extends NormalInst
case class ISetType(expr: Expr, ty: Ty) extends NormalInst

// sequence instructions
case class ISeq(insts: List[Inst]) extends Inst
