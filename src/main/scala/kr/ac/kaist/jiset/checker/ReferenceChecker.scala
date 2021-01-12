package kr.ac.kaist.jiset.checker

import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.ires.ir

object ReferenceChecker {
  def apply(algo: Algo): Boolean = {
    var defined = algo.params.toSet
    var errors = Set[String]()
    object Walker extends ir.UnitWalker {
      override def walk(inst: ir.Inst): Unit = inst match {
        case ir.ILet(x, e) =>
          walk(e); defined += x.name
        case ir.IApp(x, f, as) =>
          walk(f); walkList[ir.Expr](as, walk); defined += x.name
        case ir.IAccess(x, b, e) =>
          walk(b); walk(e); defined += x.name
        case ir.IWithCont(x, ps, b) =>
          walkList[ir.Id](ps, walk); walk(b); defined += x.name
        case _ => super.walk(inst)
      }
      override def walk(id: ir.Id): Unit =
        if (!(defined contains id.name)) errors += id.name
    }
    Walker.walk(algo.body)
    if (!errors.isEmpty) {
      println(s"[ReferenceError] ${errors.mkString(", ")}")
      println(ir.beautify(algo.body))
    }
    errors.isEmpty
  }
}
