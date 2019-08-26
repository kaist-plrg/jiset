package kr.ac.kaist.jiset.core

// CORE Expressions
sealed trait Expr extends CoreNode
case class ENum(n: Double) extends Expr {
  override def equals(that: Any): Boolean = that match {
    case that: ENum => doubleEquals(this.n, that.n)
    case _ => false
  }
  override def toString: String = {
    if (n.isNaN) "ENum(Double.NaN)"
    else if (n.isPosInfinity) "ENum(Double.PositiveInfinity)"
    else if (n.isNegInfinity) "ENum(Double.NegativeInfinity)"
    else s"ENum($n)"
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
case class ESymbol(desc: Expr) extends Expr
case class EPop(list: Expr, idx: Expr) extends Expr
case class ERef(ref: Ref) extends Expr
case class EFunc(params: List[Id], varparam: Option[Id], body: Inst) extends Expr
case class ECont(params: List[Id], body: Inst) extends Expr
case class EUOp(uop: UOp, expr: Expr) extends Expr
case class EBOp(bop: BOp, left: Expr, right: Expr) extends Expr
case class ETypeOf(expr: Expr) extends Expr
case class EIsInstanceOf(base: Expr, name: String) extends Expr {
  override def toString: String = s"""EIsInstanceOf($base, "$name")"""
}
case class EGetElems(base: Expr, name: String) extends Expr {
  override def toString: String = s"""EGetElems($base, "$name")"""
}
case class EGetSyntax(base: Expr) extends Expr
case class EParseSyntax(code: Expr, rule: Expr, flags: List[Expr]) extends Expr {
  override def toString: String = s"""EParseSyntax($code, $rule, $flags)"""
}
case class EConvert(source: Expr, target: COp, flags: List[Expr]) extends Expr
case class EContains(list: Expr, elem: Expr) extends Expr
case class ECopy(obj: Expr) extends Expr
case class EKeys(mobj: Expr) extends Expr
case class ENotSupported(msg: String) extends Expr {
  override def toString: String = s"""ENotSupported("$msg")"""
}

sealed trait COp extends CoreNode
case object CStrToNum extends COp
case object CNumToStr extends COp
case object CNumToInt extends COp
