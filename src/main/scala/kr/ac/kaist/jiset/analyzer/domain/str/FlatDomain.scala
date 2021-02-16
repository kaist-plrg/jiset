package kr.ac.kaist.jiset.analyzer.domain.str

import kr.ac.kaist.ires.ir._
import kr.ac.kaist.jiset.analyzer.domain._

object FlatDomain extends generator.FlatDomain[Str] with str.Domain {
  // string addition (+)
  def add(left: Elem, right: Elem): Elem =
    alpha((l, r) => Str(l.str + r.str))(left, right)

  // drop right (-)
  def sub(left: Elem, right: AbsINum): Elem = alpha(this, AbsINum, this) {
    case (Str(l), INum(r)) => Str(l.dropRight(r.toInt))
  }(left, right)

  // string multiplication (*)
  def mul(left: Elem, right: AbsINum): Elem = alpha(this, AbsINum, this) {
    case (Str(l), INum(r)) => Str(l * r.toInt)
  }(left, right)

  // string comparison (<)
  def lt(left: Elem, right: Elem): AbsBool =
    alpha(this, this, AbsBool)(_.str < _.str)(left, right)
}
