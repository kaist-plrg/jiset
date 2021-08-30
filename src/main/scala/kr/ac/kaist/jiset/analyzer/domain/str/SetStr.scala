package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.ir._

class SetStr(val max: Int) extends SetDomain[Str] {
  val topName = "str"
  val maxSizeOpt = Some(max)
  val totalOpt = None

  implicit class ElemOp(elem: Elem)
}
