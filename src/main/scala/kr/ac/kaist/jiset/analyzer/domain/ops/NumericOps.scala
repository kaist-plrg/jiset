package kr.ac.kaist.jiset.analyzer.domain.ops

import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.analyzer.domain._

// numeric operators
trait NumericOps { this: AbsDomain[_] =>
  // negation (-)
  val neg: Elem => Elem

  // addition (+)
  val add: (Elem, Elem) => Elem

  // substitution (-)
  val sub: (Elem, Elem) => Elem

  // multiplication (*)
  val mul: (Elem, Elem) => Elem

  // division (/)
  val div: (Elem, Elem) => Elem

  // exponential (**)
  val pow: (Elem, Elem) => Elem

  // modulo (%)
  val mod: (Elem, Elem) => Elem

  // unsigned modulo (%%)
  val umod: (Elem, Elem) => Elem

  // comparison (<)
  val lt: (Elem, Elem) => AbsBool
}

// numeric operators helper
trait NumericOpsHelper {
  type Domain <: AbsDomain[_] with NumericOps
  val Domain: Domain
  val elem: Domain.Elem

  def unary_-(): Domain.Elem = Domain.neg(elem)
  def +(that: Domain.Elem): Domain.Elem = Domain.add(elem, that)
  def -(that: Domain.Elem): Domain.Elem = Domain.sub(elem, that)
  def *(that: Domain.Elem): Domain.Elem = Domain.mul(elem, that)
  def /(that: Domain.Elem): Domain.Elem = Domain.div(elem, that)
  def **(that: Domain.Elem): Domain.Elem = Domain.pow(elem, that)
  def %(that: Domain.Elem): Domain.Elem = Domain.mod(elem, that)
  def %%(that: Domain.Elem): Domain.Elem = Domain.umod(elem, that)
  def <(that: Domain.Elem): AbsBool = Domain.lt(elem, that)
}
