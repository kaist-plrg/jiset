package kr.ac.kaist.jiset.analyzer.domain.str

import kr.ac.kaist.jiset.ir.Str
import kr.ac.kaist.jiset.analyzer.domain._

object FlatDomain extends generator.FlatDomain[Str] with str.Domain {
  // string addition (+)
  def add(left: Elem, right: Elem): Elem = alpha(_.str + _.str)(left, right)

  // drop right (-)
  def sub(left: Elem, right: AbsNum): Elem =
    alpha(this, AbsNum, this)(_ dropRight _.toInt)(left, right)

  // string multiplication (*)
  def mul(left: Elem, right: AbsNum): Elem =
    alpha(this, AbsNum, this)(_ * _.toInt)(left, right)

  // string comparison (<)
  def lt(left: Elem, right: Elem): AbsBool =
    alpha(this, this, AbsBool)(_.str < _.str)(left, right)
}
