package kr.ac.kaist.jiset.editor

import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.StateMonad
import kr.ac.kaist.jiset.util.Useful._
import scala.annotation.tailrec
import scala.collection.mutable.{ Map => MMap }
import kr.ac.kaist.jiset.editor.analyzer.domain.FlatBot
import kr.ac.kaist.jiset.editor.analyzer.domain.FlatTop
import kr.ac.kaist.jiset.editor.analyzer.domain.FlatElem
import kr.ac.kaist.jiset.js
import kr.ac.kaist.jiset.editor.analyzer.NodePoint
import kr.ac.kaist.jiset.editor.analyzer.AbsTransfer
import kr.ac.kaist.jiset.editor.analyzer.domain.AbsState
import kr.ac.kaist.jiset.cfg.Node
import kr.ac.kaist.jiset.editor.analyzer.AbsSemantics
import kr.ac.kaist.jiset.cfg.InstNode

class ReplaceExprWalker(f: Map[NodePoint[Node], AbsState], uidMap: Map[Int, NodePoint[Node]], sem: AbsSemantics) extends Walker {

  def pe(expr: Expr, astate: AbsState, np: NodePoint[Node]): Expr = {
    val helper = new sem.transfer.Helper(np)
    helper.transfer(expr)(astate)._1.getSingle match {
      case FlatElem(v: Value) => v match {
        case CompValue(ty, value: SimpleValue, targetOpt) => EComp(EConst(ty.name), simpleToExpr(value), EConst(targetOpt.getOrElse(CONST_EMPTY.name)))
        case Const(name) => EConst(name)
        case v: SimpleValue => simpleToExpr(v)
        case _ => expr
      }
      case _ => expr
    }
  }

  def simpleToExpr(v: SimpleValue): Expr = v match {
    case Num(double) => ENum(double)
    case INum(long) => EINum(long)
    case BigINum(b) => EBigINum(b)
    case Str(str) => EStr(str)
    case Bool(bool) => EBool(bool)
    case Undef => EUndef
    case Null => ENull
    case Absent => EAbsent
  }

  override def walk(inst: Inst): Inst = {
    uidMap.get(inst.uid).flatMap((np) => f.get(np).map((astate) => (np, astate))) match {
      case Some((np, astate)) => (if (astate.reachable) (inst match {
        case IIf(cond, thenInst, elseInst) => IIf(pe(cond, astate, np), walk(thenInst), walk(elseInst))
        case IWhile(cond, body) => IWhile(pe(cond, astate, np), walk(body))
        case IApp(id, fexpr, args) => IApp(id, pe(fexpr, astate, np), args.map(pe(_, astate, np)))
        case IAccess(id, bexpr, expr, args) => IAccess(id, pe(bexpr, astate, np), pe(expr, astate, np), args.map(pe(_, astate, np)))
        case IExpr(expr) => IExpr(pe(expr, astate, np))
        case ILet(id, expr) => ILet(id, pe(expr, astate, np))
        case IAssign(ref, expr) => IAssign(ref, pe(expr, astate, np))
        case IDelete(ref) => IDelete(ref)
        case IAppend(expr, list) => IAppend(pe(expr, astate, np), pe(list, astate, np))
        case IPrepend(expr, list) => IPrepend(pe(expr, astate, np), pe(list, astate, np))
        case IReturn(expr) => IReturn(pe(expr, astate, np))
        case IThrow(name) => IThrow(name)
        case IAssert(expr) => IAssert(pe(expr, astate, np))
        case IPrint(expr) => IPrint(pe(expr, astate, np))
        case IClo(id, params, captured, body) => IClo(id, params, captured, body)
        case ICont(id, params, body) => ICont(id, params, body)
        case IWithCont(id, params, body) => IWithCont(id, params, body)
        case ISeq(insts) => ISeq(insts.map(walk))
      })
      else { IExpr(EStr("empty")) })
      case None => (inst match {
        case ISeq(insts) => ISeq(insts.map(walk))
        case _ => { IExpr(EStr("empty")) }
      })
    }
  }
}

class InsensitiveUseTracker extends UnitWalker {
  val m: collection.mutable.Set[String] = collection.mutable.Set[String]()
  override def walk(inst: Inst): Unit = inst match {
    case IAssign(RefId(id), expr) => walk(expr)
    case IClo(_, _, captured, body) => { m ++ captured.map(_.name); walk(body) }
    case _ => super.walk(inst)
  }

  override def walk(ref: Ref): Unit = ref match {
    case RefId(id) => m.add(id.name)
    case RefProp(_, _) => super.walk(ref)
  }
}

class RemoveUnusedDefWalker(m: Set[String]) extends Walker {
  override def walk(inst: Inst): Inst = inst match {
    case ILet(id, _) => if (m(id.name)) inst else IExpr(EStr("empty"))
    case IAssign(RefId(id), _) => if (m(id.name)) inst else IExpr(EStr("empty"))
    case IClo(id, _, _, _) => if (m(id.name)) inst else IExpr(EStr("empty"))
    case ICont(id, _, _) => if (m(id.name)) inst else IExpr(EStr("empty"))
    case IIf(EBool(true), thenInst, _) => walk(thenInst)
    case IIf(EBool(false), _, elseInst) => walk(elseInst)
    case ISeq(insts) => {
      val ninsts = walkList[Inst](insts, walk).flatMap { case ISeq(insts) => insts; case i => List(i) }
      ISeq(ninsts.filter { case IExpr(EStr("empty")) => false; case _ => true })
    }
    case _ => super.walk(inst)
  }
}

// partial evaluator for IR functions with a given syntactic view
object PartialEval {

  def apply(view: SyntacticView): List[Algo] = {
    js.cfg // assure cfg is loaded
    val (targetAlgo, asts) = view.ast.semantics("Evaluation").get

    val instOf: Map[Int, NodePoint[Node]] = js.cfg.funcs.flatMap(f => f.nodes.collect { case n: InstNode => n }.map((v) => v.inst.uid -> NodePoint(v))).toMap
    val sem = kr.ac.kaist.jiset.editor.analyzer.AbsSemantics(view, None).fixpoint
    val rew = new ReplaceExprWalker(sem.npMap, instOf, sem)
    sem.npMap.keySet.map((np) => np.func.algoOption).flatten.map((algo) => {
      val nb = rew.walk(algo.rawBody)
      val iut = new InsensitiveUseTracker
      iut.walk(nb)
      val rud = new RemoveUnusedDefWalker(iut.m.toSet)
      val nb2 = rud.walk(nb)
      new Algo(algo.head, algo.id, nb2, algo.code)
    }).toList
  }
}
