package kr.ac.kaist.jiset.analyzer.domain.inum

import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.analyzer.domain._

object FlatDomain extends generator.FlatDomain[INum] with inum.Domain {
  // numerical operators
  def neg(elem: Elem): Elem = alpha(e => INum(-e.long))(elem)
  def add(left: Elem, right: Elem): Elem =
    alpha((l, r) => INum(l.long + r.long))(left, right)
  def sub(left: Elem, right: Elem): Elem =
    alpha((l, r) => INum(l.long - r.long))(left, right)
  def mul(left: Elem, right: Elem): Elem =
    alpha((l, r) => INum(l.long * r.long))(left, right)
  def div(left: Elem, right: Elem): Elem =
    alpha((l, r) => INum(l.long / r.long))(left, right)
  def pow(left: Elem, right: Elem): Elem = alpha((l, r) => {
    INum(math.pow(l.long, r.long).toLong)
  })(left, right)
  def mod(left: Elem, right: Elem): Elem =
    alpha((l, r) => INum(modulo(l.long, r.long).toLong))(left, right)
  def umod(left: Elem, right: Elem): Elem =
    alpha((l, r) => INum(unsigned_modulo(l.long, r.long).toLong))(left, right)
  def lt(left: Elem, right: Elem): AbsBool = alpha(this, this, AbsBool) {
    case (l, r) => Bool(l.long < r.long)
  }(left, right)

  // bit-wise operators
  def not(elem: Elem): Elem = alpha(e => INum(~e.long))(elem)
  def and(left: Elem, right: Elem): Elem =
    alpha((l, r) => INum(l.long & r.long))(left, right)
  def or(left: Elem, right: Elem): Elem =
    alpha((l, r) => INum(l.long | r.long))(left, right)
  def xor(left: Elem, right: Elem): Elem =
    alpha((l, r) => INum(l.long ^ r.long))(left, right)

  // shift operators
  def leftShift(left: Elem, right: Elem): Elem =
    alpha((l, r) => INum(l.long << r.long.toInt))(left, right)
  def rightShift(left: Elem, right: Elem): Elem =
    alpha((l, r) => INum(l.long >> r.long.toInt))(left, right)
  def unsignedRightShift(left: Elem, right: Elem): Elem =
    alpha((l, r) => INum((l.long & 0xffffffffL) >>> r.long.toInt))(left, right)
}
