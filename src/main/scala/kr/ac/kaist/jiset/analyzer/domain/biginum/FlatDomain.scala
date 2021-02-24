package kr.ac.kaist.jiset.analyzer.domain.biginum

import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.ir.state._

object FlatDomain extends generator.FlatDomain[BigINum] with biginum.Domain {
  // numerical operators
  val neg = alpha(-_)
  val add = alpha(_ + _)
  val sub = alpha(_ - _)
  val mul = alpha(_ * _)
  val div = alpha(_ / _)
  val pow = alpha(_ pow _.toInt)
  val mod = alpha(_ % _)
  val umod = alpha(unsigned_modulo(_, _))
  val lt = alpha(this, this, AbsBool)(_ < _)

  // bit-wise operators
  val not = alpha(~_)
  val and = alpha(_ & _)
  val or = alpha(_ | _)
  val xor = alpha(_ ^ _)

  // shift operators
  val leftShift = alpha(_ << _.toInt)
  val rightShift = alpha(_ >> _.toInt)
  val unsignedRightShift = (_, _) => Top
}
