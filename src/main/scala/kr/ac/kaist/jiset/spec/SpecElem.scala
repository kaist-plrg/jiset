package kr.ac.kaist.jiset.spec

import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.spec.Stringifier._

// specification components
trait SpecElem {
  // conversion to string
  override def toString: String = stringify(this)
}
