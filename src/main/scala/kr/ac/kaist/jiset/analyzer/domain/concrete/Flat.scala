package kr.ac.kaist.jiset.analyzer.domain.concrete

import kr.ac.kaist.jiset.analyzer.domain.Domain

// concrete flat domain
sealed trait Flat[+T] {
  // map function
  def map[U](f: T => U): Flat[U] = this match {
    case Zero => Zero
    case One(t) => One(f(t))
    case Many => Many
  }

  // flat map function
  def flatMap[U](f: T => Flat[U]): Flat[U] = this match {
    case Zero => Zero
    case One(t) => f(t)
    case Many => Many
  }

  // filtering
  def withFilter(f: T => Boolean): Flat[T] = this match {
    case One(t) => if (f(t)) One(t) else Zero
    case _ => this
  }

  // addition
  def ++[U >: T](that: Flat[U]): Flat[U] = (this, that) match {
    case (Zero, Zero) => Zero
    case (One(l), Zero) => One(l)
    case (Zero, One(r)) => One(r)
    case _ => Many
  }
}
case object Zero extends Flat[Nothing]
case class One[T](value: T) extends Flat[T]
case object Many extends Flat[Nothing]
