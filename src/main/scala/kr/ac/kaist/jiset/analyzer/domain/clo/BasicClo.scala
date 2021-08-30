package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Appender._
import kr.ac.kaist.jiset.util.Useful._

// basic abstract closures
object BasicClo extends Domain {
  case object Bot extends Elem
  case class CloElem(
    params: List[Id],
    locals: Map[Id, AbsValue],
    func: Function
  ) extends Elem

  // constructors
  def apply(clo: AClo): Elem = CloElem(
    clo.params,
    clo.locals,
    clo.func,
  )
  def apply(
    params: List[Id],
    locals: Map[Id, AbsValue],
    func: Function
  ): Elem = CloElem(params, locals, func)

  // appender
  implicit val app: App[Elem] = (app, elem) => elem match {
    case Bot =>
      app >> "⊥"
    case CloElem(params, locals, func) =>
      app >> AClo(params, locals, func).toString
  }

  // elements
  sealed trait Elem extends Iterable[AClo] with ElemTrait {
    // partial order
    def ⊑(that: Elem): Boolean = (this, that) match {
      case (Bot, _) => true
      case (_, Bot) => false
      case (l: CloElem, r: CloElem) => (
        l.params == r.params &&
        l.locals.keySet == r.locals.keySet &&
        l.locals.keySet.forall(x => l.locals(x) ⊑ r.locals(x)) &&
        l.func == r.func
      )
    }

    // join operator
    def ⊔(that: Elem): Elem = (this, that) match {
      case (Bot, _) => that
      case (_, Bot) => this
      case (l: CloElem, r: CloElem) if (
        l.params == r.params &&
        l.locals.keySet == r.locals.keySet &&
        l.func == r.func
      ) => CloElem(
        l.params,
        l.locals.keySet.map(x => x -> (l.locals(x) ⊔ r.locals(x))).toMap,
        l.func
      )
      case _ => exploded(s"join of closures.")
    }

    // meet operator
    def ⊓(that: Elem): Elem = (this, that) match {
      case (Bot, _) | (_, Bot) => Bot
      case (l: CloElem, r: CloElem) if (
        l.params == r.params &&
        l.locals.keySet == r.locals.keySet &&
        l.func == r.func
      ) => CloElem(
        l.params,
        l.locals.keySet.map(x => x -> (l.locals(x) ⊓ r.locals(x))).toMap,
        l.func
      )
      case _ => exploded(s"meet of closures.")
    }

    // get single value
    def getSingle: Flat[AClo] = this match {
      case Bot => FlatBot
      case CloElem(params, locals, func) => FlatElem(AClo(params, locals, func))
    }

    // iterators
    final def iterator: Iterator[AClo] = (this match {
      case Bot => None
      case CloElem(params, locals, func) => Some(AClo(params, locals, func))
    }).iterator
  }
}
