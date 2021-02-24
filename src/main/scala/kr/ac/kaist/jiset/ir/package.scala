package kr.ac.kaist.jiset

package object ir {
  // beautify
  def beautify(
    node: IRNode,
    indent: String = "",
    detail: Boolean = true,
    index: Boolean = false,
    exprId: Boolean = false
  ): String = Beautifier.beautify(node, indent, detail, index, exprId)

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

  // power
  def lpow(l: Long, r: Long): Long = math.pow(l.toDouble, r.toDouble).toLong

  // modulo
  def unsigned_modulo(l: Long, r: Long): Long =
    { val m = l % r; if (m * r < 0) m + r else m }
  def unsigned_modulo(l: BigInt, r: BigInt): BigInt =
    { val m = l % r; if (m * r < 0) m + r else m }
  def unsigned_modulo(l: Double, r: Double): Double =
    { val m = l % r; if (m * r < 0) m + r else m }

  // normalize strings
  def norm(str: String): String =
    str.replace("\\", "\\\\").replace("\"", "\\\"")
      .replace("\n", "\\n").replace("\b", "\\b")

  // singleton types
  type Null = Null.type
  type Undef = Undef.type
  type Absent = Absent.type
}
