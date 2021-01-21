package kr.ac.kaist.jiset.util

object Conversion {
  implicit def int2InfNum(n: Int): InfNum = Num(n)
  implicit def infNum2Int(i: InfNum): Int = i match {
    case Num(n) => n
    case PInf => Int.MaxValue
    case MInf => Int.MinValue
  }
  implicit def infNumOrdering[T <: InfNum]: Ordering[T] =
    Ordering.by(_.toInt)
}
