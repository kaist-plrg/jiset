package kr.ac.kaist.jiset.analyzer.domain.biginum

import kr.ac.kaist.ires.ir._
import kr.ac.kaist.jiset.analyzer.domain._

object FlatDomain extends generator.FlatDomain[BigINum] with biginum.Domain {
  // numerical operators
  def neg(elem: Elem): Elem = alpha(e => BigINum(-e.bigint))(elem)
  def add(left: Elem, right: Elem): Elem =
    alpha((l, r) => BigINum(l.bigint + r.bigint))(left, right)
  def sub(left: Elem, right: Elem): Elem =
    alpha((l, r) => BigINum(l.bigint - r.bigint))(left, right)
  def mul(left: Elem, right: Elem): Elem =
    alpha((l, r) => BigINum(l.bigint * r.bigint))(left, right)
  def div(left: Elem, right: Elem): Elem = ??? // TODO
  def pow(left: Elem, right: Elem): Elem =
    alpha((l, r) => BigINum(l.bigint.pow(r.bigint.toInt)))(left, right)
  def mod(left: Elem, right: Elem): Elem =
    alpha((l, r) => BigINum(modulo(l.bigint, r.bigint)))(left, right)
  def umod(left: Elem, right: Elem): Elem =
    alpha((l, r) => BigINum(unsigned_modulo(l.bigint, r.bigint)))(left, right)
  def lt(left: Elem, right: Elem): AbsBool = alpha(this, this, AbsBool) {
    case (l, r) => Bool(l.bigint < r.bigint)
  }(left, right)

  // bit-wise operators
  def not(elem: Elem): Elem = alpha(e => BigINum(~e.bigint))(elem)
  def and(left: Elem, right: Elem): Elem =
    alpha((l, r) => BigINum(l.bigint & r.bigint))(left, right)
  def or(left: Elem, right: Elem): Elem =
    alpha((l, r) => BigINum(l.bigint | r.bigint))(left, right)
  def xor(left: Elem, right: Elem): Elem =
    alpha((l, r) => BigINum(l.bigint ^ r.bigint))(left, right)

  // shift operators
  def leftShift(left: Elem, right: Elem): Elem =
    alpha((l, r) => BigINum(l.bigint << r.bigint.toInt))(left, right)
  def rightShift(left: Elem, right: Elem): Elem =
    alpha((l, r) => BigINum(l.bigint >> r.bigint.toInt))(left, right)
  def unsignedRightShift(left: Elem, right: Elem): Elem = Top
}
