package kr.ac.kaist.jiset.util

case class Span(start: Pos = Pos(), end: Pos = Pos())
case class Pos(line: Int = -1, column: Int = -1)
