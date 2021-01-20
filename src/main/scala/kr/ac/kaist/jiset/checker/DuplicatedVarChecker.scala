package kr.ac.kaist.jiset.checker

import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.spec.algorithm.Algo
import kr.ac.kaist.ires.ir._

object DuplicatedVarChecker extends Checker {
  // for specifications
  def apply(
    spec: ECMAScript,
    targets: List[Algo]
  ): List[DuplicatedVariable] = targets.flatMap(DuplicatedVarChecker.apply)

  // for algorithms
  def apply(algo: Algo): Option[DuplicatedVariable] = {
    var mayDefined = Set[String]()
    var errors = Set[String]()
    def check(name: String): Unit = {
      if (mayDefined contains name) errors += name
      else mayDefined += name
    }
    algo.params.foreach(param => check(param.name))
    object Walker extends UnitWalker {
      override def walk(inst: Inst): Unit = inst match {
        case ILet(x, e) =>
          walk(e); check(x.name)
        case IApp(x, f, as) =>
          walk(f); walkList[Expr](as, walk); check(x.name)
        case IAccess(x, b, e) =>
          walk(b); walk(e); mayDefined += x.name
        case IWithCont(x, ps, b) =>
          walkList[Id](ps, walk); walk(b); check(x.name)
        case IIf(cond, thenInst, elseInst) =>
          walk(cond)
          // save the current mayDefined
          val oldMayDefined = mayDefined

          // calculate mayDefined in thenInst
          walk(thenInst)
          val thenMayDefined = mayDefined

          // reset the old mayDefined and calculate mayDefined in elseInst
          mayDefined = oldMayDefined
          walk(elseInst)
          val elseMayDefined = mayDefined

          // merge mayDefined in thenInst and elseInst
          mayDefined = thenMayDefined ++ elseMayDefined
        case _ => super.walk(inst)
      }
    }
    Walker.walk(algo.body)
    if (errors.isEmpty) None
    else Some(DuplicatedVariable(algo, errors))
  }
}
