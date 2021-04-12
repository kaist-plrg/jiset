package kr.ac.kaist.jiset

import java.text.Normalizer._
import kr.ac.kaist.jiset.util.Useful.error
import scala.{ BigInt => SBigInt }

package object ir {
  // equality between doubles
  def doubleEquals(left: Double, right: Double): Boolean = {
    if (left.isNaN && right.isNaN) true
    else if (isNegZero(left) && !isNegZero(right)) false
    else if (!isNegZero(left) && isNegZero(right)) false
    else left == right
  }

  // triple quotations
  val TRIPLE = "\"\"\""

  // negative zero check
  def isNegZero(double: Double): Boolean = (1 / double).isNegInfinity

  // normalize strings
  def norm(str: String): String =
    str.replace("\\", "\\\\").replace("\"", "\\\"")
      .replace("\n", "\\n").replace("\b", "\\b")

  def toERef(name: String): ERef = ERef(RefId(Id(name)))
  def toRef(name: String): Ref = RefId(Id(name))
}
