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

  // modulo operation for bigints
  def modulo(l: BigInt, r: BigInt): BigInt = {
    l % r
  }

  // unsigned modulo operation for bigints
  def unsigned_modulo(l: BigInt, r: BigInt): BigInt = {
    val m = l % r
    if (m * r < 0) m + r
    else m
  }

  // get proper number value
  def number(x: BigDecimal): Value = {
    if (x.toLong == x) INum(x.toLong)
    else Num(x.toDouble)
  }

  // negative zero check
  def isNegZero(double: Double): Boolean = (1 / double).isNegInfinity

  def toERef(name: String): ERef = ERef(RefId(Id(name)))
  def toRef(name: String): Ref = RefId(Id(name))

  // get completion
  def getCompletion(st: State)(
    value: Value,
    ty: Value = NamedAddr("CONST_normal"),
    target: Value = NamedAddr("CONST_empty")
  ): Addr = st.allocMap(Ty("Completion"), Map(
    Str("Value") -> value,
    Str("Type") -> ty,
    Str("Target") -> target,
  ))

  // conversion number to string
  def toStringHelper(m: Double, radix: Int = 10): String = {
    if (m.isNaN) "NaN"
    else if (m == 0) "0"
    else if (m < 0) "-" + toStringHelper(-m, radix)
    else if (m.isPosInfinity) "Infinity"
    else {
      var s = BigDecimal(m)
      var n = 0
      while (s % radix == BigDecimal(0) || s % 1 != BigDecimal(0)) {
        if (s % radix == BigDecimal(0)) { s /= radix; n += 1 }
        else { s *= radix; n -= 1 }
      }
      while ((((s - (s % radix)) / radix) * BigDecimal(radix).pow(n + 1)).toDouble == m) {
        s = (s - (s % radix)) / radix
        n = n + 1
      }
      var sLong = s.toLong
      var k = 0
      while (s >= BigDecimal(1)) { s /= radix; k += 1 }
      n += k
      if (k <= n && n <= 21) {
        getStr(sLong, radix) + ("0" * (n - k))
      } else if (0 < n && n <= 21) {
        val str = getStr(sLong, radix)
        str.substring(0, n) + '.' + str.substring(n)
      } else if (-6 < n && n <= 0) {
        "0." + ("0" * (-n)) + getStr(sLong, radix)
      } else if (k == 1) {
        getStr(sLong, radix) + "e" + getSign(n) + math.abs(n - 1).toString
      } else {
        val str = getStr(sLong, radix)
        str.substring(0, 1) + '.' + str.substring(1) + 'e' + getSign(n) + math.abs(n - 1).toString
      }
    }
  }

  // get sign
  def getSign(n: Int): Char = if (n - 1 > 0) '+' else '-'

  // get string of number
  def getStr(number: Long, radix: Int): String = {
    var str = ""
    var sLong = number
    while (sLong > 0) { str += getRadixString(sLong % radix); sLong /= radix }
    str.reverse
  }

  // get radix string of number
  def getRadixString(d: Long): String = {
    if (d < 10) d.toString else ('a' + (d - 10)).toChar.toString
  }
}
