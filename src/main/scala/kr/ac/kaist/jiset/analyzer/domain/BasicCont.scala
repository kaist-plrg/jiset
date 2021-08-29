package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Appender._
import kr.ac.kaist.jiset.util.Useful._

// basic abstract continuations
object BasicCont extends Domain {
  case object Bot extends Elem
  case class ContElem(
    params: List[Id],
    locals: Map[Id, AbsValue],
    target: NodePoint[Node]
  ) extends Elem

  // constructors
  def apply(clo: ACont): Elem = ContElem(
    clo.params,
    clo.locals,
    clo.target,
  )
  def apply(
    params: List[Id],
    locals: Map[Id, AbsValue],
    target: NodePoint[Node]
  ): Elem = ContElem(params, locals, target)

  // appender
  implicit val app: App[Elem] = (app, elem) => elem match {
    case Bot =>
      app >> "⊥"
    case ContElem(params, locals, target) =>
      app >> ACont(params, locals, target).toString
  }

  // elements
  sealed trait Elem extends Iterable[ACont] with ElemTrait {
    // partial order
    def ⊑(that: Elem): Boolean = (this, that) match {
      case (Bot, _) => true
      case (_, Bot) => false
      case (l: ContElem, r: ContElem) => (
        l.params == r.params &&
        l.locals.keySet == r.locals.keySet &&
        l.locals.keySet.forall(x => l.locals(x) ⊑ r.locals(x)) &&
        l.target == r.target
      )
    }

    // join operator
    def ⊔(that: Elem): Elem = (this, that) match {
      case (Bot, _) => that
      case (_, Bot) => this
      case (l: ContElem, r: ContElem) if (
        l.params == r.params &&
        l.locals.keySet == r.locals.keySet &&
        l.target == r.target
      ) => ContElem(
        l.params,
        l.locals.keySet.map(x => x -> (l.locals(x) ⊔ r.locals(x))).toMap,
        l.target
      )
      case _ => exploded(s"join of continuations.")
    }

    // meet operator
    def ⊓(that: Elem): Elem = (this, that) match {
      case (Bot, _) | (_, Bot) => Bot
      case (l: ContElem, r: ContElem) if (
        l.params == r.params &&
        l.locals.keySet == r.locals.keySet &&
        l.target == r.target
      ) => ContElem(
        l.params,
        l.locals.keySet.map(x => x -> (l.locals(x) ⊓ r.locals(x))).toMap,
        l.target
      )
      case _ => exploded(s"meet of continuations.")
    }

    // get single value
    def getSingle: Flat[ACont] = this match {
      case Bot => FlatBot
      case ContElem(params, locals, target) => FlatElem(ACont(params, locals, target))
    }

    // iterators
    final def iterator: Iterator[ACont] = (this match {
      case Bot => None
      case ContElem(params, locals, target) => Some(ACont(params, locals, target))
    }).iterator
  }
}
