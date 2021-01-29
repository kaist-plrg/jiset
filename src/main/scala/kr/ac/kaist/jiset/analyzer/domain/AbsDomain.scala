package kr.ac.kaist.jiset.analyzer.domain

// abstract domain for value V
trait AbsDomain[V] extends Domain {
  // abstraction functions for values
  def alpha(v: V): Elem
  def alpha(set: Set[V]): Elem = set.foldLeft(Bot) {
    case (elem, v) => elem ⊔ alpha(v)
  }
  def alpha(seq: V*): Elem = alpha(seq.toSet)

  // unary operator abstraction
  def alpha(f: V => V): Elem => Elem = _.gamma match {
    case Infinite => Top
    case Finite(set) => alpha(set.map(f))
  }

  // binary operator abstraction
  def alpha(f: (V, V) => V): (Elem, Elem) => Elem =
    (x, y) => (x.gamma, y.gamma) match {
      case (Infinite, _) | (_, Infinite) => Top
      case (Finite(lset), Finite(rset)) => alpha(for {
        l <- lset
        r <- rset
      } yield f(l, r))
    }

  // constructor
  def apply(set: Set[V]): Elem = alpha(set)

  // extractor
  def apply(seq: V*): Elem = alpha(seq.toSet)

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
