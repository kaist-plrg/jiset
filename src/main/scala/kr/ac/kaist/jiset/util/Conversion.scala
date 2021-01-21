package kr.ac.kaist.jiset.util

object Conversion {
  implicit def int2InfNum(n: Int): InfNum = Num(n)
}
