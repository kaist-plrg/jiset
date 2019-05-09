package kr.ac.kaist.ase.util

trait Appendable {
  val TAB = "  "
  def appendTo(sb: StringBuilder, pre: String): StringBuilder
  override def toString: String = appendTo(new StringBuilder, "").toString
}
