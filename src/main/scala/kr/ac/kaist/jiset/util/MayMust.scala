package kr.ac.kaist.jiset.util

case class MayMust[T](may: Set[T], must: Set[T]) {
  def ++(that: MayMust[T]): MayMust[T] = MayMust(
    this.may ++ that.may,
    this.must intersect that.must
  )
  def +(elem: T): MayMust[T] = MayMust(may + elem, must + elem)
}
object MayMust {
  def apply[T](set: Set[T]): MayMust[T] = MayMust(set, set)
}
