package kr.ac.kaist.jiset.ir

// IR Expressions
sealed trait Expr extends IRNode { var uid: Int = -1 }

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
case class EINum(n: Long) extends Expr {
  override def toString: String = s"EINum(${n}L)"
}
case class EBigINum(b: BigInt) extends Expr {
  override def toString: String = s"""EBigINum(BigInt("$b"))"""
}
case class EStr(str: String) extends Expr {
  override def toString: String = s"EStr($TRIPLE$str$TRIPLE)"
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
case class ECont(params: List[Id], body: Inst) extends Expr
case class EUOp(uop: UOp, expr: Expr) extends Expr
case class EBOp(bop: BOp, left: Expr, right: Expr) extends Expr
case class ETypeOf(expr: Expr) extends Expr
case class EIsCompletion(expr: Expr) extends Expr
case class EIsInstanceOf(base: Expr, name: String) extends Expr {
  override def toString: String = s"EIsInstanceOf($base, $TRIPLE$name$TRIPLE)"
}
case class EGetElems(base: Expr, name: String) extends Expr {
  override def toString: String = s"EGetElems($base, $TRIPLE$name$TRIPLE)"
}
case class EGetSyntax(base: Expr) extends Expr
case class EParseSyntax(code: Expr, rule: Expr, flags: Expr) extends Expr
case class EConvert(source: Expr, target: COp, flags: List[Expr]) extends Expr
case class EContains(list: Expr, elem: Expr) extends Expr
case class EReturnIfAbrupt(expr: Expr, check: Boolean) extends Expr
case class ECopy(obj: Expr) extends Expr
case class EKeys(mobj: Expr) extends Expr
case class ENotSupported(msg: String) extends Expr {
  override def toString: String = s"ENotSupported($TRIPLE$msg$TRIPLE)"
}

sealed trait COp extends IRNode
case object CStrToNum extends COp
case object CStrToBigInt extends COp
case object CNumToStr extends COp
case object CNumToInt extends COp
case object CNumToBigInt extends COp
case object CBigIntToNum extends COp
