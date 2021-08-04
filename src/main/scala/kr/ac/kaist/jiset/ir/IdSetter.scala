package kr.ac.kaist.jiset.ir

class IdSetter extends UnitWalker {
  // allocation sites
  private var asiteCount: Int = 0
  private def newAsite: Int = { val id = asiteCount; asiteCount += 1; id }

  // instruction unique ids
  private var iidCount: Int = 0
  private def newIId: Int = { val id = iidCount; iidCount += 1; id }
  private var iidMap: Map[Int, Inst] = Map()
  def getIIdMap: Map[Int, Inst] = iidMap

  // expression unique ids
  private var eidCount: Int = 0
  private def newEId: Int = { val id = eidCount; eidCount += 1; id }
  private var eidMap: Map[Int, Expr] = Map()
  def getEIdMap: Map[Int, Expr] = eidMap

  override def walk(inst: Inst): Unit = {
    inst match {
      case inst: AllocSite => inst.setASite(newAsite)
      case _ =>
    }
    val iid = newIId
    inst.setUId(iid)
    iidMap += iid -> inst
    super.walk(inst)
  }
  override def walk(expr: Expr): Unit = {
    expr match {
      case expr: AllocSite => expr.setASite(newAsite)
      case _ =>
    }
    val eid = newEId
    expr.setUId(eid)
    eidMap += eid -> expr
    super.walk(expr)
  }
}
