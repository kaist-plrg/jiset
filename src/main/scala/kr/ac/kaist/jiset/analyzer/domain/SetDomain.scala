package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Appender._
import kr.ac.kaist.jiset.util.Useful._

// set domain
trait SetDomain[A] extends Domain {
  // name of top element
  protected val topName: String

  // total elemetns
  protected val totalOpt: Option[Iterable[A]]

  // elements
  lazy val Bot = Base(Set())
  object Top extends Elem
  case class Base(set: Set[A]) extends Elem

  // abstraction functions
  def apply(elems: A*): Elem = this(elems)
  def apply(elems: Iterable[A]): Elem = Base(elems.toSet)

  // appender
  implicit val app: App[Elem] = (app, elem) => elem match {
    case Top => app >> topName
    case Base(set) => app >> (set.size match {
      case 0 => "⊥"
      case 1 => set.head.toString
      case _ => set.toList.map(_.toString).sorted.mkString("{", ", ", "}")
    })
  }

  // elements
  sealed trait Elem extends Iterable[A] with ElemTrait {
    // partial order
    def ⊑(that: Elem): Boolean = (this, that) match {
      case (_, Top) => true
      case (Top, _) => false
      case (Base(lset), Base(rset)) => lset subsetOf rset
    }

    // join operator
    def ⊔(that: Elem): Elem = (this, that) match {
      case (Top, _) | (_, Top) => Top
      case (Base(lset), Base(rset)) => Base(lset ++ rset)
    }

    // get single value
    def getSingle: Flat[A] = this match {
      case Base(set) => set.size match {
        case 0 => FlatBot
        case 1 => FlatElem(set.head)
        case _ => FlatTop
      }
      case _ => FlatTop
    }

    // iterators
    final def iterator: Iterator[A] = (this match {
      case Base(set) => set
      case Top => totalOpt.getOrElse {
        warn("impossible to concretize the top value.")
        Nil
      }
    }).iterator

    // contains check
    def contains(elem: A): Boolean = this match {
      case Top => true
      case Base(set) => set contains elem
    }
  }
}
object SetDomain {
  // constructors
  def apply[A](topName: String): SetDomain[A] = this(topName, None)
  def apply[A](
    topName: String,
    totalOpt: Option[Iterable[A]]
  ): SetDomain[A] = {
    val (n, t) = (topName, totalOpt)
    new SetDomain[A] {
      protected val topName: String = n
      protected val totalOpt: Option[Iterable[A]] = t
    }
  }
}
