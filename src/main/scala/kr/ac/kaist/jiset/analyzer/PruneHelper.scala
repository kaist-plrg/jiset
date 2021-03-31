package kr.ac.kaist.jiset.analyzer

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
    val pruneRef = PruneRef(st, pass)
    st => expr match {
      case _ if !PRUNE => error("no prune")
      case pruneRef(ref, t) => ref match {
        case RefId(Id(x)) =>
          if (t.isBottom) AbsState.Bot else st.define(x, t)
        case _ => st
      }
      case _ => st
    }
  }

  case class PruneRef(st: AbsState, pass: Boolean) {
    val prune = this
    def not: PruneRef = PruneRef(st, !pass)
    def unapply(expr: Expr): Option[(Ref, AbsType)] = optional(this(expr))
    private def apply(expr: Expr): (Ref, AbsType) = expr match {
      case EUOp(ONot, expr) => not(expr)
      case EBOp(OEq, ERef(ref), right) =>
        val ((l, r), _) = (for {
          a <- transfer(ref)
          l <- get(_.lookup(a, check = false))
          r <- transfer(right)
        } yield (l.escaped, r.escaped))(st)
        (ref, pruneValue(l, r, pass))
      case EIsInstanceOf(base @ ERef(ref), name) =>
        val (l, _) = (for {
          t <- transfer(base)
        } yield t.escaped)(st)
        (ref, pruneInstance(l, name, pass))
      case EBOp(OEq, ETypeOf(left @ ERef(ref)), right) =>
        val ((l, r), _) = (for {
          a <- transfer(ref)
          l <- get(_.lookup(a, check = false))
          r <- transfer(right)
        } yield (l.escaped, r.escaped))(st)
        (ref, pruneType(l, r, pass))
      case EBOp(OOr, prune(lref, lt), prune(rref, rt)) if lref == rref =>
        (lref, if (pass) lt ⊔ rt else lt ⊓ rt)
      case EBOp(OAnd, prune(lref, lt), prune(rref, rt)) if lref == rref =>
        (lref, if (pass) lt ⊓ rt else lt ⊔ rt)
      case _ => error("failed")
    }
  }

  // purning for value checks
  def pruneValue(l: AbsType, r: AbsType, pass: Boolean): AbsType = {
    optional[AbsType](if (pass) l ⊓ r else r.set.head match {
      case t: SingleT if r.set.size == 1 => l - t
    }).getOrElse(l)
  }

  // purning for type checks
  def pruneType(l: AbsType, r: AbsType, pass: Boolean): AbsType = {
    optional[AbsType](r.set.head match {
      case Str(name) if r.set.size == 1 =>
        val t: Type = name match {
          case "Object" => NameT("Object")
          case "Symbol" => SymbolT
          case "Number" => NumT
          case "BigInt" => BigIntT
          case "String" => StrT
          case "Boolean" => BoolT
          case "Undefined" => Undef
          case "Null" => Null
        }
        if (pass) l ⊓ t.abs else l - t
    }).getOrElse(l)
  }

  // pruning for instance checks
  def pruneInstance(l: AbsType, name: String, pass: Boolean): AbsType = {
    val t = NameT(name)
    l.ast ⊔ (if (pass) l ⊓ t.abs else l - t)
  }
}
