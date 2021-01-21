package kr.ac.kaist.jiset.util

import Conversion._

trait InfNum {
  def +(i: InfNum): InfNum = ((this, i): @unchecked) match {
    case (Num(n0), Num(n1)) => {
      val res = n0 + n1
      if (n0 > 0 && n1 > 0 && res < 0) PInf
      else if (n0 < 0 && n1 < 0 && res > 0) MInf
      else res
    }
    case (PInf, _) | (_, PInf) => PInf
    case (MInf, _) | (_, MInf) => MInf
  }
}

case class Num(n: Int) extends InfNum
case object PInf extends InfNum
case object MInf extends InfNum
