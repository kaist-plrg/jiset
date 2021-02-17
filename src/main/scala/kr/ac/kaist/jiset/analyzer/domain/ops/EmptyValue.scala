package kr.ac.kaist.jiset.analyzer.domain.ops

import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.analyzer.domain._

// bit-wise operators
trait EmptyValue { this: AbsDomain[_] =>
  // empty value
  val Empty: Elem
}
