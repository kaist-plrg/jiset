package kr.ac.kaist.jiset.analyzer.domain.concrete

import scala.collection.immutable.{ Set => SSet }

// concrete finite set domain
sealed trait Set[+T] {
  // map function
  def map[U](f: T => U): Set[U] = this match {
    case Finite(set) => Finite(set.map(f))
    case Infinite => Infinite
  }

  // flat map function
  def flatMap[U](f: T => Set[U]): Set[U] = this match {
    case Finite(set) => set.map(f).foldLeft[Set[U]](Finite())(_ ++ _)
    case Infinite => Infinite
  }

  // filtering
  def withFilter(f: T => Boolean): Set[T] = this match {
    case Finite(set) => Finite(set filter f)
    case Infinite => Infinite
  }

  // conversion to set
  def toList: List[T] = this match {
    case Finite(set) => set.toList
    case Infinite => ???
  }

  // addition
  def ++[U >: T](that: Set[U]): Set[U] = (this, that) match {
    case (Finite(l), Finite(r)) => Finite(l ++ r: SSet[U])
    case _ => Infinite
  }
}
case object Infinite extends Set[Nothing]
case class Finite[T](values: SSet[T]) extends Set[T]
object Finite { def apply[T](seq: T*): Finite[T] = Finite(seq.toSet) }
