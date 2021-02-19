package kr.ac.kaist.jiset.util

import kr.ac.kaist.jiset.LINE_SEP

trait MapPrinter {
  class Builder(tab: String = "  ") {
    val sb: StringBuilder = new StringBuilder
    var k = 0
    val endl = LINE_SEP
    def indent = tab * k
    def >>(str: String): Builder = { sb ++= str; this }
    def >>(pr: MapPrinter): Builder = pr match {
      case MPBase(str) => this >> str
      case MPMap(map) =>
        this >> "{" >> endl
        k += 1
        for ((k, v) <- map) this >> indent >> k >> ": " >> v >> endl
        k -= 1
        this >> indent >> "}" >> endl
    }
    override def toString: String = sb.toString
  }

  // conversion to string
  override def toString: String = (new Builder >> this).toString
}
object MapPrinter {
  // implicit conversion to MapPrinter
  implicit def str2mp(str: String) = MPBase(str)
  implicit def apply(map: Map[String, MapPrinter]) = MPMap(map)
}
case class MPBase(str: String) extends MapPrinter
case class MPMap(map: Map[String, MapPrinter]) extends MapPrinter
