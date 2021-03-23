package kr.ac.kaist.jiset.analyzer

case class Result[+T](elem: T, st: AbsState) {
  def toPair: (T, AbsState) = (elem, st)
}
