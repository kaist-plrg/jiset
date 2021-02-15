package kr.ac.kaist.jiset.spec.algorithm

import kr.ac.kaist.jiset.util.Useful._

class Name private (val str: String) {
  override val toString = str
}
object Name {
  def normalize(s: String) = getScalaName(s)
  def apply(name: String): Name = new Name(normalize(name))
  def unapply(n: Name): Option[String] = Some(n.str)
}
