package kr.ac.kaist.jiset.spec.algorithm

case class Param(
    name: String
) {
  // conversion to string
  override def toString: String = name
}
