package kr.ac.kaist.jiset.analyzer.domain.concrete

import scala.collection.immutable.{ Set => SSet }

// concrete finite set domain
sealed trait Set[+T] {
  // map function
  def map[U](f: T => U): Set[U] = this match {
    case Finite(set) => Finite(set.map(f))
    case Infinite => Infinite
  }

  // conversion to string
  override def toString: String = this match {
    case Finite(set) if set.isEmpty => "⊥"
    case Finite(set) => "[" + set.mkString(", ") + "]"
    case Infinite => "⊤"
  }
}
case object Infinite extends Set[Nothing]
case class Finite[T](values: SSet[T]) extends Set[T]
object Finite { def apply[T](seq: T*): Finite[T] = Finite(seq.toSet) }
