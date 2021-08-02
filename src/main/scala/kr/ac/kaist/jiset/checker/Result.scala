package kr.ac.kaist.jiset.checker

case class Result[+T](elem: T, st: AbsState) {
  def toPair: (T, AbsState) = (elem, st)
}
