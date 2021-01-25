package kr.ac.kaist.jiset.analyzer.domain.concrete

// concrete finite set domain
sealed trait FinSet[+T] {
  override def toString: String = this match {
    case Finite(set) if set.isEmpty => "⊥"
    case Finite(set) => "[" + set.mkString(", ") + "]"
    case Infinite => "⊤"
  }
}
case object Infinite extends FinSet[Nothing]
case class Finite[T](values: Set[T]) extends FinSet[T]
object Finite { def apply[T](seq: T*): Finite[T] = Finite(seq.toSet) }
