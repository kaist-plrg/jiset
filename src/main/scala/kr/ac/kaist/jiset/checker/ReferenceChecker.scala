package kr.ac.kaist.jiset.checker

import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.spec.algorithm.Algo
import kr.ac.kaist.jiset.util.MayMust
import kr.ac.kaist.ires.ir._

object ReferenceChecker extends Checker {
  // for specifications
  def apply(
    spec: ECMAScript,
    targets: List[Algo]
  ): List[Result] = targets.flatMap(apply(spec.globals, _))

  // for algorithms
  def apply(global: Set[String], algo: Algo): List[Result] = {
    val initial = global ++ algo.params.map(_.name).toSet
    var (defined, unused) = (MayMust(initial), Set[String]())
    var undefined = Set[String]()
    var duplicated = Set[String]()

    // define a new variable
    def define(name: String): Unit = {
      if (defined.may contains name) duplicated += name
      defined += name
      unused += name
    }

    // use a variable
    def use(name: String): Unit = {
      if (!(initial ++ defined.must).contains(name) &&
        !name.startsWith("CONST_")) undefined += name
      unused -= name
    }

    // algorithm body walker
    object Walker extends UnitWalker {
      override def walk(inst: Inst): Unit = inst match {
        case ILet(x, e) =>
          walk(e); define(x.name)
        case IApp(x, f, as) =>
          walk(f); walkList[Expr](as, walk); define(x.name)
        case IAccess(x, b, e) =>
          walk(b); walk(e); define(x.name)
        case IWithCont(x, ps, b) =>
          walkList[Id](ps, walk); walk(b); define(x.name)
        case IIf(cond, thenInst, elseInst) =>
          walk(cond)
          // save the current status
          val (oldDefined, oldUnused) = (defined, unused)

          // calculate status in thenInst
          walk(thenInst)
          val (thenDefiend, thenUnused) = (defined, unused)

          // reset the old status and calculate status in elseInst
          defined = oldDefined
          unused = oldUnused
          walk(elseInst)
          val (elseDefiend, elseUnused) = (defined, unused)

          // merge mayDefined in thenInst and elseInst
          defined = thenDefiend ++ elseDefiend
          unused = thenUnused intersect elseUnused
        case _ => super.walk(inst)
      }
      override def walk(id: Id): Unit = {
        use(id.name)
      }
    }
    Walker.walk(algo.body)

    var res = List[Result]()
    if (!undefined.isEmpty) res ::= Undefined(algo, undefined)
    if (!unused.isEmpty) res ::= Unused(algo, unused)
    if (!duplicated.isEmpty) res ::= Duplicated(algo, duplicated)
    res
  }

  // results
  abstract class Result(prefix: String) extends Bug {
    // bug name
    val name: String = prefix

    // algorithm
    val algo: Algo

    // identifier names
    val ids: Set[String]

    // bug message
    val msg: String = s"${algo.name}: ${ids.mkString(", ")}"
  }

  // undefined variables
  case class Undefined(algo: Algo, ids: Set[String]) extends Result("Undefined")

  // unused variables
  case class Unused(algo: Algo, ids: Set[String]) extends Result("Unused")

  // duplicated variables
  case class Duplicated(algo: Algo, ids: Set[String]) extends Result("Duplicated")
}
