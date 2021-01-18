package kr.ac.kaist.jiset.spec.algorithm

case class Param(name: String, optional: Boolean = false) {
  // conversion to string
  override def toString: String =
    name + (if (optional) "?" else "")
}
