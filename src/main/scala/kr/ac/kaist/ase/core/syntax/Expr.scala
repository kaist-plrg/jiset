package kr.ac.kaist.ase.core

// CORE Expressions
sealed trait Expr extends CoreNode
case class ENum(n: Double) extends Expr {
  override def equals(that: Any): Boolean = that match {
    case that: ENum => doubleEquals(this.n, that.n)
    case _ => false
  }
  override def toString: String = {
    if (n.isNaN) "ENum(Double.NaN)" else s"ENum($n)"
  }
}
case class EINum(n: Long) extends Expr
case class EStr(str: String) extends Expr {
  override def toString: String = s"""EStr("$str")"""
}
case class EBool(b: Boolean) extends Expr
case object EUndef extends Expr
case object ENull extends Expr
case object EAbsent extends Expr
case class EMap(ty: Ty, props: List[(Expr, Expr)]) extends Expr
case class EList(exprs: List[Expr]) extends Expr
case class EPop(list: Expr) extends Expr
case class ERef(ref: Ref) extends Expr
case class EFunc(params: List[Id], varparam: Option[Id], body: Inst) extends Expr
case class EApp(fexpr: Expr, args: List[Expr]) extends Expr
case class EUOp(uop: UOp, expr: Expr) extends Expr
case class EBOp(bop: BOp, left: Expr, right: Expr) extends Expr
case class EExist(ref: Ref) extends Expr
case class ETypeOf(expr: Expr) extends Expr
case class EIsInstanceOf(base: Expr, name: String) extends Expr {
  override def toString: String = s"""EIsInstanceOf($base, "$name")"""
}
case class EGetSyntax(base: Expr) extends Expr
case class EContains(list: Expr, elem: Expr) extends Expr
case class ENotYetImpl(msg: String) extends Expr {
  override def toString: String = s"""ENotYetImpl("$msg")"""
}
