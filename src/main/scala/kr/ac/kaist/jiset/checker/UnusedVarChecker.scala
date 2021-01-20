package kr.ac.kaist.jiset.checker

import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.spec.algorithm.{ Algo, Head, SyntaxDirectedHead }
import kr.ac.kaist.ires.ir._
import org.jsoup.nodes.Document.OutputSettings.Syntax

sealed trait VarStatus
case object Unused extends VarStatus
case object SyntaxUnused extends VarStatus
case object ParamUnused extends VarStatus
case object Used extends VarStatus

object UnusedVarChecker extends Checker {
  // for specifications
  def apply(
    spec: ECMAScript,
    targets: List[Algo]
  ): List[UnusedVariable] = targets.flatMap(UnusedVarChecker(spec.globals, _))

  // for algorithms
  def apply(initial: Set[String], algo: Algo): Option[UnusedVariable] = {
    var scopeVars: Map[String, VarStatus] = algo.head match {
      case _: SyntaxDirectedHead => algo.params.map(_.name -> SyntaxUnused).toMap
      case _ => algo.params.map(_.name -> ParamUnused).toMap
    }
    object Walker extends UnitWalker {
      override def walk(inst: Inst): Unit = inst match {
        case ILet(x, e) =>
          walk(e); scopeVars += x.name -> Unused
        case IApp(x, f, as) =>
          walk(f); walkList[Expr](as, walk); scopeVars += x.name -> Unused
        case IAccess(x, b, e) =>
          walk(b); walk(e); scopeVars += x.name -> Unused
        case IWithCont(x, ps, b) =>
          walkList[Id](ps, walk); walk(b); scopeVars += x.name -> Unused
        case _ => super.walk(inst)
      }
      override def walk(id: Id): Unit = {
        val name = id.name
        if (scopeVars.keySet.exists(_ == name)) {
          scopeVars += name -> Used
        }
      }
    }
    Walker.walk(algo.body)
    val errors = scopeVars.filter(t => t._2 != Used && t._2 != SyntaxUnused).keySet
    if (errors.isEmpty) None
    else Some(UnusedVariable(algo, errors))
  }
}
