package kr.ac.kaist.jiset.checker

import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.checker.Stringifier._

// type checker components
trait CheckerElem {
  // conversion to string
  override def toString: String = stringify(this)
}
