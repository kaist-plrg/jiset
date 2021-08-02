package kr.ac.kaist.jiset.analyzer.domain

// simple domain
class SimpleDomain[T](val value: T) extends Domain {
  object Bot extends Elem
  object Top extends Elem

  // elements
  sealed trait Elem extends ElemTrait {
    // partial order
    def ⊑(that: Elem): Boolean = (this, that) match {
      case BasicOrder(bool) => bool
    }

    // join operator
    def ⊔(that: Elem): Elem = (this, that) match {
      case BasicJoin(elem) => elem
    }

    // get single value
    def getSingle: Flat[value.type] = this match {
      case Bot => FlatBot
      case Top => FlatElem(value)
    }
  }
}
