package kr.ac.kaist.jiset.checker

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.ir.Beautifier._
import kr.ac.kaist.jiset.util.Useful._
import scala.Console.RED

trait PruneHelper { this: AbsTransfer.Helper =>
  import AbsState.monad._

  // pruning abstract states
  def prune(
    st: AbsState,
    expr: Expr,
    pass: Boolean
  ): Updater = {
    val pruneVar = PruneVar(st, pass)
    st => expr match {
      case _ if !PRUNE => st
      case pruneVar(newSt) => newSt
      case _ => st
    }
  }

  case class PruneVar(st: AbsState, pass: Boolean) {
    // pruned variable names
    var prunedVars: Set[String] = Set()

    // escape pruned variables
    private def escaped(st: AbsState): AbsState = AbsState(
      reachable = st.reachable,
      map = {
        (st.map.map {
          case (v, atype) if prunedVars contains v => v -> atype.uncheckEscaped
          case (v, atype) => v -> atype
        }).toMap
      }
    )

    // helper
    val prune = this
    def not: PruneVar = PruneVar(st, !pass)
    def unapply(expr: Expr): Option[AbsState] = optional(this(expr))

    // prune state
    private def apply(expr: Expr): AbsState = expr match {
      case EUOp(ONot, expr) => not(expr)
      // prune normal completion
      case EBOp(OEq, rexpr @ ERef(RefProp(ref: RefId, EStr("Type"))), EConst("normal")) =>
        val updator = for {
          a <- transfer(ref)
          l <- get(_.lookup(ERef(ref), a, check = false))
          newT = pruneNormalComp(l, pass)
        } yield (ERef(ref), a, newT)
        update(st, updator)
      case EBOp(OEq, lexpr @ ERef(ref @ RefId(Id(x))), right) =>
        val updator = for {
          a <- transfer(ref)
          l <- get(_.lookup(lexpr, a, check = false))
          r <- transfer(right)
          newT = pruneValue(l.escaped(lexpr), r.escaped(right), pass)
        } yield (lexpr, a, newT)
        prunedVars += x
        update(st, updator)
      case EIsInstanceOf(base @ ERef(ref @ RefId(Id(x))), name) =>
        val updator = for {
          a <- transfer(ref)
          l <- get(_.lookup(base, a, check = false))
          newT = pruneInstance(l.escaped(base), name, pass)
        } yield (base, a, newT)
        prunedVars += x
        update(st, updator)
      case EBOp(OEq, ETypeOf(left @ ERef(ref @ RefId(Id(x)))), right) =>
        val updator = for {
          a <- transfer(ref)
          l <- get(_.lookup(left, a, check = false))
          r <- transfer(right)
          newT = pruneType(l.escaped(left), r.escaped(right), pass)
        } yield (left, a, newT)
        prunedVars += x
        update(st, updator)
      case EBOp(OOr, prune(st0), prune(st1)) =>
        val est0 = escaped(st0)
        val est1 = escaped(st1)
        if (pass) est0 ⊔ est1 else est0 ⊓ est1
      case EBOp(OAnd, prune(st0), prune(st1)) =>
        val est0 = escaped(st0)
        val est1 = escaped(st1)
        if (pass) est0 ⊓ est1 else est0 ⊔ est1
      case _ => st
    }
  }

  // update state
  private def update(
    st: AbsState,
    updator: Result[(Expr, AbsRef, AbsType)]
  ): AbsState = {
    val ((rexpr, aref, newT), newSt) = updator(st)
    if (newT.isBottom) AbsState.Bot
    else newSt.update(rexpr, aref, newT)
  }

  // pruning for normal completion
  def pruneNormalComp(l: AbsType, pass: Boolean): AbsType =
    if (pass) AbsType(l.compSet) - AbruptT else l ⊓ AbruptT

  // pruning for value checks
  def pruneValue(l: AbsType, r: AbsType, pass: Boolean): AbsType = {
    optional[AbsType](if (pass) l ⊓ r else r.set.head match {
      case t: SingleT if r.set.size == 1 => l - t
    }).getOrElse(l)
  }

  // pruning for type checks
  def pruneType(l: AbsType, r: AbsType, pass: Boolean): AbsType = {
    optional[AbsType](r.set.head match {
      case AStr(name) if r.set.size == 1 =>
        val t: Type = name match {
          case "Object" => NameT("Object")
          case "Reference" => NameT("ReferenceRecord")
          case "Symbol" => SymbolT
          case "Number" => NumT
          case "BigInt" => BigIntT
          case "String" => StrT
          case "Boolean" => BoolT
          case "Undefined" => AUndef
          case "Null" => ANull
        }
        if (pass) l ⊓ t.abs else l - t
    }).getOrElse(l)
  }

  // pruning for instance checks
  def pruneInstance(l: AbsType, name: String, pass: Boolean): AbsType = {
    val nameT = NameT(name)
    val astT = AstT(name)
    val isAst = cfg.spec.grammar.recSubs.keySet contains name
    val prevAstT = AbsType(cfg.spec.grammar.recSubs.getOrElse(name, Set()).map(AstT(_): Type))
    (pass, isAst) match {
      case (false, false) => l - nameT
      case (false, true) => (l - astT) ⊔ (prevAstT - astT)
      case (true, false) => l ⊓ nameT.abs
      case (true, true) => prevAstT ⊓ astT.abs
    }
  }
}
