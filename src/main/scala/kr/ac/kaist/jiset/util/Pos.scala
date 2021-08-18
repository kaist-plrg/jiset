package kr.ac.kaist.jiset.util

case class Pos(
  line: Int = -1,
  column: Int = -1,
  index: Int = -1
) {
  // validity check
  def valid: Boolean = (
    line != -1 &&
    column != -1 &&
    index != -1
  )

  // conversion to string
  override def toString: String = toString(useIndex = false)
  def toString(useIndex: Boolean): String = {
    if (!valid) "?"
    else if (useIndex) s"$index"
    else s"$line:$column"
  }
}
