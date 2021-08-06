package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Appender._
import kr.ac.kaist.jiset.util.Useful._

// flat domain
trait FlatDomain[A] extends Domain {
  // name of top element
  protected val topName: String

  // total elemetns
  protected val totalOpt: Option[Iterable[A]]

  // elements
  object Bot extends Elem
  object Top extends Elem
  case class Base(elem: A) extends Elem

  // abstraction functions
  def apply(elems: A*): Elem = this(elems)
  def apply(elems: Iterable[A]): Elem = elems.size match {
    case 0 => Bot
    case 1 => Base(elems.head)
    case _ => Top
  }

  // appender
  implicit val app: App[Elem] = (app, elem) => elem match {
    case Bot => app >> "⊥"
    case Top => app >> topName
    case Base(v) => app >> v.toString
  }

  // elements
  sealed trait Elem extends Iterable[A] with ElemTrait {
    // partial order
    def ⊑(that: Elem): Boolean = (this, that) match {
      case BasicOrder(bool) => bool
      case (Base(l), Base(r)) => l == r
    }

    // join operator
    def ⊔(that: Elem): Elem = (this, that) match {
      case BasicJoin(elem) => elem
      case (Base(l), Base(r)) => if (l == r) this else Top
    }

    // get single value
    def getSingle: Flat[A] = this match {
      case Bot => FlatBot
      case Top => FlatTop
      case Base(elem) => FlatElem(elem)
    }

    // iterators
    final def iterator: Iterator[A] = (this match {
      case Bot => Nil
      case Base(elem) => List(elem)
      case Top => totalOpt.getOrElse {
        warn("impossible to concretize the top value.")
        Nil
      }
    }).iterator

    // contains check
    def contains(elem: A): Boolean = this match {
      case Bot => false
      case Top => true
      case Base(x) => x == elem
    }
  }
}
object FlatDomain {
  // constructors
  def apply[A](topName: String): FlatDomain[A] = this(topName, None)
  def apply[A](
    topName: String,
    totalOpt: Option[Iterable[A]]
  ): FlatDomain[A] = {
    val (n, t) = (topName, totalOpt)
    new FlatDomain[A] {
      protected val topName: String = n
      protected val totalOpt: Option[Iterable[A]] = t
    }
  }
}
