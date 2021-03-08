package kr.ac.kaist.jiset.util

trait InfNum {
  import InfNum._

  def +(i: InfNum): InfNum = ((this, i): @unchecked) match {
    case (IntNum(n0), IntNum(n1)) => {
      val res = n0 + n1
      if (n0 > 0 && n1 > 0 && res < 0) PInf
      else if (n0 < 0 && n1 < 0 && res > 0) MInf
      else res
    }
    case (PInf, _) | (_, PInf) => PInf
    case (MInf, _) | (_, MInf) => MInf
  }

  override def toString: String = this match {
    case IntNum(n) => n.toString
    case PInf => "+∞"
    case MInf => "-∞"
  }
}

case class IntNum(n: Int) extends InfNum
case object PInf extends InfNum
case object MInf extends InfNum

// implicit conversions
object InfNum {
  implicit def int2InfNum(n: Int): InfNum = IntNum(n)
  implicit def infNum2Int(i: InfNum): Int = i match {
    case IntNum(n) => n
    case PInf => Int.MaxValue
    case MInf => Int.MinValue
  }
  implicit def infNumOrdering[T <: InfNum]: Ordering[T] =
    Ordering.by(_.toInt)
}
