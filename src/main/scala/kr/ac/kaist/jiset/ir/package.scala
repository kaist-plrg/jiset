package kr.ac.kaist.jiset

package object ir {
  // completion type
  type CompletionType = CompletionType.Value

  // equality between doubles
  def doubleEquals(left: Double, right: Double): Boolean = {
    if (left.isNaN && right.isNaN) true
    else if (isNegZero(left) && !isNegZero(right)) false
    else if (!isNegZero(left) && isNegZero(right)) false
    else left == right
  }

  // modulo operation
  def modulo(l: Double, r: Double): Double = {
    l % r
  }

  // unsigned modulo operation
  def unsigned_modulo(l: Double, r: Double): Double = {
    val m = l % r
    if (m * r < 0.0) m + r
    else m
  }

  // negative zero check
  def isNegZero(double: Double): Boolean = (1 / double).isNegInfinity

  def toERef(name: String): ERef = ERef(RefId(Id(name)))
  def toRef(name: String): Ref = RefId(Id(name))
}
