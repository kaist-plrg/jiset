package kr.ac.kaist.jiset.parser.algorithm

import kr.ac.kaist.jiset.ir._

class ASiteWalker extends UnitWalker {
  private var count: Int = 0
  private def getCount: Int = { val result = count; count += 1; result }
  override def walk(inst: Inst): Unit = {
    inst match {
      case (ithrow: IThrow) => ithrow.asite = getCount
      case _ =>
    }
    super.walk(inst)
  }
  override def walk(expr: Expr): Unit = {
    expr match {
      case expr: AllocExpr => expr.asite = getCount
      case _ =>
    }
    super.walk(expr)
  }
}
