package kr.ac.kaist.jiset.checker

import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.spec.algorithm.{ Algo, Param }
import kr.ac.kaist.jiset.util.InfNum
import kr.ac.kaist.ires.ir._

object ArityChecker extends Checker {
  // for specifications
  def apply(
    spec: ECMAScript,
    targets: List[Algo]
  ): List[Result] = targets.flatMap(apply(spec.globals, _))

  // for algorithms
  def apply(globals: Set[String], algo: Algo): List[Result] = {
    // algorithm body walker
    object Walker extends UnitWalker {
      override def walk(inst: Inst): Unit = inst match {
        // case IApp(x, f, as) =>
        //   walk(f); walklist[expr](as, walk);
        // case ilet(x, e) =>
        //   walk(e);
        // case iaccess(x, b, e) =>
        //   walk(b); walk(e);
        // case iwithcont(x, ps, b) =>
        //   walkList[id](ps, walk); walk(b);
        case _ => super.walk(inst)
      }
      override def walk(id: Id): Unit = {
        // use(id.name)
      }
    }
    Walker.walk(algo.body)
    List.empty
  }

  // result
  class Result(
      algo: Algo,
      target: Algo,
      params: List[Param]
  ) extends Bug {
    // bug name
    val name: String = "Arity Mismatch"

    // bug message
    val msg: String =
      s"${algo.name}: To call ${target.name}, ${target.arity} argument(s) should be supplied(current : ${params.length})"
  }
}
