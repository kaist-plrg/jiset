package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.ir._

class SetStr(val max: Int) extends SetDomain[Str] with StrDomain {
  val topName = "str"
  val maxSizeOpt = Some(max)
  val totalOpt = None

  implicit class ElemOp(elem: Elem) extends StrOp {
    def plus(that: Elem): Elem = (elem, that) match {
      case (Base(lset), Base(rset)) => alpha(for {
        Str(l) <- lset
        Str(r) <- rset
      } yield Str(l + r))
      case _ => Top
    }
    def plusNum(that: AbsNum): Elem = (elem, that.getSingle) match {
      case (Base(lset), FlatElem(Num(r))) => Base(for {
        Str(l) <- lset
      } yield Str(l + Character.toChars(r.toInt).mkString("")))
      case _ => Top
    }
  }
}
