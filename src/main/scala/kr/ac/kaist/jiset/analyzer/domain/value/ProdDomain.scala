package kr.ac.kaist.jiset.analyzer.domain.value

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.analyzer.domain._

object ProdDomain extends value.Domain {
  // abstraction functions
  def alpha(v: Value): Elem = v match {
    case pure: PureValue => Elem(pure = AbsPure(pure))
    case comp: Completion => Elem(comp = AbsComp(comp))
  }

  // bottom value
  val Bot: Elem = Elem()

  // top value
  val Top: Elem = Elem(
    AbsPure.Top,
    AbsComp.Top,
  )

  // constructor
  def apply(
    pure: AbsPure = AbsPure.Bot,
    comp: AbsComp = AbsComp.Bot
  ): Elem = Elem(pure, comp)

  case class Elem(
    private val pure: AbsPure = AbsPure.Bot,
    comp: AbsComp = AbsComp.Bot
  ) extends ElemTrait {
    // partial order
    def ⊑(that: Elem): Boolean = (
      this.pure ⊑ that.pure &&
      this.comp ⊑ that.comp
    )

    // join operator
    def ⊔(that: Elem): Elem = Elem(
      this.pure ⊔ that.pure,
      this.comp ⊔ that.comp
    )

    // meet operator
    def ⊓(that: Elem): Elem = Elem(
      this.pure ⊓ that.pure,
      this.comp ⊓ that.comp
    )

    // concretization clotion
    def gamma: concrete.Set[Value] = (
      this.pure.gamma ++
      this.comp.gamma
    )

    // conversion to flat domain
    def getSingle: concrete.Flat[Value] = (
      this.pure.getSingle ++
      this.comp.getSingle
    )

    // escape completions
    def escaped: AbsPure = pure ⊔ comp.value
  }
}
