package kr.ac.kaist.jiset.analyzer.domain.comp

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.analyzer.domain._

object BasicDomain extends comp.Domain {
  // abstraction functions
  def alpha(comp: Completion): Elem =
    Elem(Map(comp.ty -> (AbsPure(comp.value), AbsPure(comp.target))))
  def alpha(v: PureValue): Elem =
    Elem(Map(CompNormal -> (AbsPure(v), AbsPure.Top)))

  // list of types
  val tys: List[CompletionType] = CompletionType.all

  // bottom value
  val Bot: Elem = Elem()

  // top value
  val Top: Elem = Elem(tys.map(t => t -> (AbsPure.Top, AbsPure.Top)).toMap)

  // normalized map
  private def norm(map: Map[CompletionType, (AbsPure, AbsPure)]) = (map.collect {
    case (k, (v, t)) if !v.isBottom && !t.isBottom => k -> (v, t)
  }).toMap

  // constructors
  def apply(pairs: (CompletionType, (AbsPure, AbsPure))*): Elem = Elem(pairs.toMap)

  // extractor
  def unapply(elem: Elem): Option[Map[CompletionType, (AbsPure, AbsPure)]] =
    Some(elem.map)

  case class Elem(
    map: Map[CompletionType, (AbsPure, AbsPure)] = Map()
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

    // prune
    def prune(v: PureValue): Elem = Elem(this.map.map {
      case (CompNormal, (absV, target)) => CompNormal -> (absV.prune(v), target)
      case abrupt @ _ => abrupt
    })

    // concretization function
    def gamma: concrete.Set[Completion] = Infinite // TODO

    // conversion to flat domain
    def getSingle: concrete.Flat[Completion] = Many // TODO

    // lookup
    def apply(ty: CompletionType): (AbsPure, AbsPure) =
      map.getOrElse(ty, (AbsPure.Bot, AbsPure.Bot))

    // get only abrupt completion
    def abrupt: Elem = Elem(map - CompNormal)

    // check normal
    def isNormal: Set[Boolean] = map.keySet.map {
      case CompNormal => true
      case _ => false
    }

    // get value
    def value: AbsPure = map.foldLeft(AbsPure.Bot) { case (l, (_, (r, _))) => l ⊔ r }
  }
}
