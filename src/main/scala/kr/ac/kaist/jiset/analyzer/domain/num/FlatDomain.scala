package kr.ac.kaist.jiset.analyzer.domain.num

import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.analyzer.domain._

object FlatDomain extends generator.FlatDomain[Num] with num.Domain {
  // numerical operators
  val neg = alpha(-_)
  val add = alpha(_ + _)
  val sub = alpha(_ - _)
  val mul = alpha(_ * _)
  val div = alpha(_ / _)
  val pow = alpha(math.pow(_, _))
  val mod = alpha(_ % _)
  val umod = alpha(unsigned_modulo(_, _))
  val lt = alpha(this, this, AbsBool)(_ < _)
}
