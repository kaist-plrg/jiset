package kr.ac.kaist.jiset.analyzer.domain.num

import kr.ac.kaist.ires.ir._
import kr.ac.kaist.jiset.analyzer.domain._

object FlatDomain extends generator.FlatDomain[Num] with num.Domain {
  // numerical operators
  def neg(elem: Elem): Elem = alpha(e => Num(-e.double))(elem)
  def add(left: Elem, right: Elem): Elem =
    alpha((l, r) => Num(l.double + r.double))(left, right)
  def sub(left: Elem, right: Elem): Elem =
    alpha((l, r) => Num(l.double - r.double))(left, right)
  def mul(left: Elem, right: Elem): Elem =
    alpha((l, r) => Num(l.double * r.double))(left, right)
  def div(left: Elem, right: Elem): Elem =
    alpha((l, r) => Num(l.double / r.double))(left, right)
  def pow(left: Elem, right: Elem): Elem =
    alpha((l, r) => Num(math.pow(l.double, r.double)))(left, right)
  def mod(left: Elem, right: Elem): Elem =
    alpha((l, r) => Num(modulo(l.double, r.double)))(left, right)
  def umod(left: Elem, right: Elem): Elem =
    alpha((l, r) => Num(unsigned_modulo(l.double, r.double)))(left, right)
  def lt(left: Elem, right: Elem): AbsBool = alpha(this, this, AbsBool) {
    case (l, r) => Bool(l.double < r.double)
  }(left, right)
}
