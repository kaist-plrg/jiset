package kr.ac.kaist.jiset.checker

import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.spec.algorithm.Algo
import kr.ac.kaist.ires.ir._

object ReferenceChecker extends Checker {
  // for specifications
  def apply(
    spec: ECMAScript,
    targets: List[Algo]
  ): List[ReferenceError] = targets.flatMap(ReferenceChecker(spec.globals, _))

  // for algorithms
  def apply(initial: Set[String], algo: Algo): Option[ReferenceError] = {
    var defined: Set[String] = initial ++ algo.params.map(_.name).toSet
    var errors = Set[String]()
    object Walker extends UnitWalker {
      override def walk(inst: Inst): Unit = inst match {
        case ILet(x, e) =>
          walk(e); defined += x.name
        case IApp(x, f, as) =>
          walk(f); walkList[Expr](as, walk); defined += x.name
        case IAccess(x, b, e) =>
          walk(b); walk(e); defined += x.name
        case IWithCont(x, ps, b) =>
          walkList[Id](ps, walk); walk(b); defined += x.name
        case _ => super.walk(inst)
      }
      override def walk(id: Id): Unit = {
        val name = id.name
        if (!defined.contains(name) && !name.startsWith("CONST_")) errors += name
      }
    }
    Walker.walk(algo.body)
    if (errors.isEmpty) None
    else Some(ReferenceError(algo, errors))
  }
}
