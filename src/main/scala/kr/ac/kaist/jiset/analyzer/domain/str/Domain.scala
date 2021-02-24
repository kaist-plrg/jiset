package kr.ac.kaist.jiset.analyzer.domain.str

import kr.ac.kaist.jiset.ir.Str
import kr.ac.kaist.jiset.analyzer.domain._

// string abstract domain
trait Domain extends AbsDomain[Str] {
  // string addition (+)
  def add(left: Elem, right: Elem): Elem

  // drop right (-)
  def sub(left: Elem, right: AbsINum): Elem

  // string multiplication (*)
  def mul(left: Elem, right: AbsINum): Elem

  // string comparison (<)
  def lt(left: Elem, right: Elem): AbsBool
}
