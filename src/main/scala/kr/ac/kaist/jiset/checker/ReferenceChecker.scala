package kr.ac.kaist.jiset.checker

import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.spec.algorithm.Algo
import kr.ac.kaist.ires.ir

object ReferenceChecker {
  def apply(
    initial: Set[String],
    algo: Algo
  ): Boolean = {
    var defined: Set[String] = initial ++ algo.params.map(_.name).toSet
    var scopeVars: Map[String, Boolean] = algo.params.map(_.name -> false).toMap
    var errors = Set[String]()
    object Walker extends ir.UnitWalker {
      override def walk(inst: ir.Inst): Unit = inst match {
        case ir.ILet(x, e) => {
          walk(e)
          scopeVars += x.name -> false
          defined += x.name
        }
        case ir.IApp(x, f, as) => {
          walk(f)
          walkList[ir.Expr](as, walk)
          scopeVars += x.name -> false
          defined += x.name
        }
        case ir.IAccess(x, b, e) => {
          walk(b)
          walk(e)
          scopeVars += x.name -> false
          defined += x.name
        }
        case ir.IWithCont(x, ps, b) => {
          walkList[ir.Id](ps, walk)
          walk(b);
          scopeVars += x.name -> false
          defined += x.name
        }

        case _ => super.walk(inst)
      }
      override def walk(id: ir.Id): Unit = {
        val name = id.name
        if (!defined.contains(name) && !name.startsWith("CONST_")) errors += name
        else if (scopeVars.keySet.exists(_ == name)) {
          scopeVars += name -> true
        }
      }
    }
    Walker.walk(algo.body)
    if (!errors.isEmpty)
      println(s"[ReferenceError] ${algo.name}: ${errors.mkString(", ")}")
    val unusedVar = scopeVars.filter(t => !t._2).keySet.mkString(", ")
    if (!unusedVar.isEmpty)
      println(s"[UnusedVarWarning] ${algo.name}: ${unusedVar}")
    errors.isEmpty
  }
}
