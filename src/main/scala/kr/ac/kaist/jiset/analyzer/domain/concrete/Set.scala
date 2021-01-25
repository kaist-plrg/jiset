package kr.ac.kaist.jiset.analyzer.domain.concrete

import scala.collection.immutable.{ Set => SSet }

// concrete finite set domain
sealed trait Set[+T] {
  override def toString: String = this match {
    case Finite(set) if set.isEmpty => "⊥"
    case Finite(set) => "[" + set.mkString(", ") + "]"
    case Infinite => "⊤"
  }
}
case object Infinite extends Set[Nothing]
case class Finite[T](values: SSet[T]) extends Set[T]
object Finite { def apply[T](seq: T*): Finite[T] = Finite(seq.toSet) }
