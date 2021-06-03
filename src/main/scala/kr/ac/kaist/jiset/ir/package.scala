package kr.ac.kaist.jiset

package object ir {
  // equality between doubles
  def doubleEquals(left: Double, right: Double): Boolean = {
    if (left.isNaN && right.isNaN) true
    else if (isNegZero(left) && !isNegZero(right)) false
    else if (!isNegZero(left) && isNegZero(right)) false
    else left == right
  }

  // negative zero check
  def isNegZero(double: Double): Boolean = (1 / double).isNegInfinity

  def toERef(name: String): ERef = ERef(RefId(Id(name)))
  def toRef(name: String): Ref = RefId(Id(name))
}
