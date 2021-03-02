package kr.ac.kaist.jiset.analyzer.domain.comp

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.analyzer.domain._

object BasicDomain extends comp.Domain {
  // abstraction functions
  def alpha(comp: Completion): Elem =
    Elem(Map(comp.ty -> (AbsPure(comp.value), AbsPure(comp.target))))

  // list of types
  val tys: List[String] = List(
    "normal", "throw", "continue", "return", "break"
  )

  // bottom value
  val Bot: Elem = Elem()

  // top value
  val Top: Elem = Elem(tys.map(t => t -> (AbsPure.Top, AbsPure.Top)).toMap)

  // normalized map
  private def norm(map: Map[String, (AbsPure, AbsPure)]) = (map.collect {
    case (k, (v, t)) if !v.isBottom && !t.isBottom => k -> (v, t)
  }).toMap

  case class Elem(
    map: Map[String, (AbsPure, AbsPure)] = Map()
  ) extends ElemTrait {
    // partial order
    def ⊑(that: Elem): Boolean = tys.forall(t => {
      val (thisVal, thisTarget) = this(t)
      val (thatVal, thatTarget) = that(t)
      thisVal ⊑ thatVal && thisTarget ⊑ thatTarget
    })

    // join operator
    def ⊔(that: Elem): Elem = Elem(norm(tys.map(t => {
      val (thisVal, thisTarget) = this(t)
      val (thatVal, thatTarget) = that(t)
      t -> (thisVal ⊔ thatVal, thisTarget ⊔ thatTarget)
    }).toMap))

    // meet operator
    def ⊓(that: Elem): Elem = Elem(norm(tys.map(t => {
      val (thisVal, thisTarget) = this(t)
      val (thatVal, thatTarget) = that(t)
      t -> (thisVal ⊓ thatVal, thisTarget ⊓ thatTarget)
    }).toMap))

    // concretization function
    def gamma: concrete.Set[Completion] = Infinite // TODO

    // conversion to flat domain
    def getSingle: concrete.Flat[Completion] = Many // TODO

    // lookup
    def apply(ty: String): (AbsPure, AbsPure) =
      map.getOrElse(ty, (AbsPure.Bot, AbsPure.Bot))

    // check normal
    def isNormal: Set[Boolean] = map.keySet.map {
      case "normal" => true
      case _ => false
    }

    // get value
    def value: AbsPure = map.foldLeft(AbsPure.Bot) { case (l, (_, (r, _))) => l ⊔ r }
  }
}
