package kr.ac.kaist.jiset.analyzer.domain.concrete

import kr.ac.kaist.jiset.analyzer.domain.Domain

// concrete flat domain
sealed trait Flat[+T] {
  override def toString: String = this match {
    case Zero => "⊥"
    case One(t) => s"$t"
    case Many => "⊤"
  }
}
case object Zero extends Flat[Nothing]
case class One[T](value: T) extends Flat[T]
case object Many extends Flat[Nothing]
