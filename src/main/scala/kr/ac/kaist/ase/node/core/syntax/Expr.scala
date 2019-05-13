package kr.ac.kaist.ase.node.core

// CORE Expressions
sealed trait Expr extends CoreNode
case class ENum(n: Double) extends Expr {
  override def equals(that: Any): Boolean = that match {
    case that: ENum => doubleEquals(this.n, that.n)
    case _ => false
  }
}
case class EINum(n: Long) extends Expr
case class EStr(str: String) extends Expr
case class EBool(b: Boolean) extends Expr
case object EUndef extends Expr
case object ENull extends Expr
case class ERef(ref: Ref) extends Expr
case class EFunc(params: List[Id], body: Inst) extends Expr
case class EUOp(uop: UOp, expr: Expr) extends Expr
case class EBOp(bop: BOp, left: Expr, right: Expr) extends Expr
case class EExist(ref: Ref) extends Expr
case class ETypeOf(expr: Expr) extends Expr
