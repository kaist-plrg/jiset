package kr.ac.kaist.jiset.extractor.algorithm

import kr.ac.kaist.jiset.ir._

class LocWalker extends UnitWalker {
  private var asiteCount: Int = 0
  private def newAsite: Int = { val result = asiteCount; asiteCount += 1; result }
  private var csiteCount: Int = 0
  private def newCsite: Int = { val result = csiteCount; csiteCount += 1; result }
  override def walk(inst: Inst): Unit = {
    inst match {
      case (ithrow: IThrow) => ithrow.asite = newAsite
      case (call: CallInst) => call.csite = newCsite
      case _ =>
    }
    super.walk(inst)
  }
  override def walk(expr: Expr): Unit = {
    expr match {
      case expr: AllocExpr => expr.asite = newAsite
      case _ =>
    }
    super.walk(expr)
  }
}
