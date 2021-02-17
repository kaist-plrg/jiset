package kr.ac.kaist.jiset

package object analyzer {
  // equality between doubles
  def doubleEquals(left: Double, right: Double): Boolean = {
    if (left.isNaN && right.isNaN) true
    else if (isNegZero(left) && !isNegZero(right)) false
    else if (!isNegZero(left) && isNegZero(right)) false
    else left == right
  }

  // negative zero check
  def isNegZero(double: Double): Boolean = (1 / double).isNegInfinity

  // triple quotations
  val TRIPLE = "\"\"\""

  // modulo
  def modulo(l: BigInt, r: BigInt): BigInt = l % r
  def unsigned_modulo(l: BigInt, r: BigInt): BigInt = {
    val m = l % r
    if (m * r < 0) m + r
    else m
  }
  def modulo(l: BigInt, r: Long): BigInt = l % r
  def unsigned_modulo(l: BigInt, r: Long): BigInt = {
    val m = l % r
    if (m * r < 0) m + r
    else m
  }
  def modulo(l: Double, r: Double): Double = l % r
  def unsigned_modulo(l: Double, r: Double): Double = {
    val m = l % r
    if (m * r < 0.0) m + r
    else m
  }

  // singleton types
  type Null = Null.type
  type Undef = Undef.type
  type Absent = Absent.type
}
