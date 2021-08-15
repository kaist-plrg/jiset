package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.util.WeakUId

// IR Expressions
sealed trait Expr extends IRElem with WeakUId
object Expr extends Parser[Expr]
case class ENum(n: Double) extends Expr {
  override def equals(that: Any): Boolean = that match {
    case that: ENum => doubleEquals(this.n, that.n)
    case _ => false
  }
}
case class EINum(n: Long) extends Expr
case class EBigINum(b: BigInt) extends Expr
case class EStr(str: String) extends Expr
case class EBool(b: Boolean) extends Expr
case object EUndef extends Expr
case object ENull extends Expr
case object EAbsent extends Expr
case class EConst(name: String) extends Expr
case class EComp(ty: Expr, value: Expr, target: Expr) extends Expr
case class EMap(ty: Ty, props: List[(Expr, Expr)]) extends Expr with AllocSite
case class EList(exprs: List[Expr]) extends Expr with AllocSite
case class ESymbol(desc: Expr) extends Expr with AllocSite
case class EPop(list: Expr, idx: Expr) extends Expr
case class ERef(ref: Ref) extends Expr
case class EUOp(uop: UOp, expr: Expr) extends Expr
case class EBOp(bop: BOp, left: Expr, right: Expr) extends Expr
case class ETypeOf(expr: Expr) extends Expr
case class EIsCompletion(expr: Expr) extends Expr
case class EIsInstanceOf(base: Expr, name: String) extends Expr with AllocSite
case class EGetElems(base: Expr, name: String) extends Expr with AllocSite
case class EGetSyntax(base: Expr) extends Expr
case class EParseSyntax(code: Expr, rule: Expr, parserParams: List[Boolean]) extends Expr
case class EConvert(source: Expr, target: COp, flags: List[Expr]) extends Expr
case class EContains(list: Expr, elem: Expr) extends Expr
case class EReturnIfAbrupt(expr: Expr, check: Boolean) extends Expr
case class ECopy(obj: Expr) extends Expr with AllocSite
case class EKeys(mobj: Expr, intSorted: Boolean) extends Expr with AllocSite
case class ENotSupported(msg: String) extends Expr with AllocSite
