package kr.ac.kaist.jiset.analyzer.domain

// abstract domain for value V
trait AbsDomain[V] extends Domain {
  // abstraction functions for values
  def alpha(v: V): Elem
  def alpha(set: Set[V]): Elem = set.foldLeft(Bot) {
    case (elem, v) => elem ⊔ alpha(v)
  }
  def alpha(seq: V*): Elem = alpha(seq.toSet)

  // constructor
  def apply(set: Set[V]): Elem = alpha(set)

  // extractor
  def apply(seq: V*): Elem = alpha(seq.toSet)

  // abstraction functions for operators
  def alpha[U, D <: EAbsDomain[U]](
    f: V => U
  )(domain: D): Elem => domain.Elem = elem => elem.gamma match {
    case Infinite => domain.Top
    case Finite(vset) => domain.alpha(vset.map(f(_)))
  }
  def alpha[U, D <: EAbsDomain[U]](
    f: (V, V) => U
  )(domain: D): (Elem, Elem) => domain.Elem = (l, r) => (l.gamma, r.gamma) match {
    case (Finite(lset), Finite(rset)) => domain.alpha(lset.foldLeft(Set[U]()) {
      case (set, l) => set ++ rset.map(f(l, _))
    })
    case _ => domain.Top
  }

  // abstract element
  type Elem <: ElemTrait

  // abstract element traits
  trait ElemTrait extends super.ElemTrait { this: Elem =>
    // concretization function
    def gamma: concrete.Set[V]

    // conversion to flat domain
    def getSingle: concrete.Flat[V]
  }
}
