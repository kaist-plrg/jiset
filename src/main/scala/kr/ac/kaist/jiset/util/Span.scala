package kr.ac.kaist.jiset.util

case class Span(
  start: Pos = Pos(),
  end: Pos = Pos()
) {
  // validity check
  def valid: Boolean = start.valid && end.valid

  // conversion to string
  override def toString: String = toString(useIndex = false)
  def toString(useIndex: Boolean): String = {
    val Pos(sl, sc, si) = start
    val Pos(el, ec, ei) = end
    if (!valid) ""
    else if (useIndex) s"$si-$ei"
    else if (sl == el) s"$sl:$sc-$ec"
    else s"$start-$end"
  }
}
