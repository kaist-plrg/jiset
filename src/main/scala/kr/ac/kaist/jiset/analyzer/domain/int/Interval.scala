package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.analyzer.exploded
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Appender._
import kr.ac.kaist.jiset.util.Useful._

object IntervalInt extends IntDomain {
  // elements
  case object Bot extends Elem
  case class Single(int: INum) extends Elem
  case class Interval(from: Long, to: Long) extends Elem
  case object Top extends Elem

  // appender
  implicit val app: App[Elem] = (app, elem) => app >> (elem match {
    case Bot => "⊥"
    case Top => "int"
    case Single(elem) => elem.toString
    case Interval(from, to) =>
      s"[$from, $to]"
  })

  // get intervals
  def getInterval(from: Long, to: Long): Elem = {
    if (from > to) Bot
    else if (from == to) Single(INum(from))
    else Interval(from, to)
  }

  // abstraction functions
  def apply(elems: INum*): Elem = this(elems)
  def apply(elems: Iterable[INum]): Elem = alpha(elems)
  def alpha(elems: Iterable[INum]): Elem = elems.size match {
    case 0 => Bot
    case 1 => Single(elems.head)
    case _ => {
      val ints = elems.map(_.long)
      Interval(ints.min, ints.max)
    }
  }

  // elements
  sealed trait Elem extends Iterable[INum] with ElemTrait {
    // partial order
    def ⊑(that: Elem): Boolean = (this, that) match {
      case (Bot, _) => true
      case (_, Bot) => false
      case (Interval(lfrom, lto), Interval(rfrom, rto)) =>
        (rfrom <= lfrom) && (lto <= rto)
      case (l, r) => l.toInterval ⊑ r.toInterval
    }

    // join operator
    def ⊔(that: Elem): Elem = (this, that) match {
      case (Bot, _) | (_, Top) => that
      case (_, Bot) | (Top, _) => this
      case (Interval(lfrom, lto), Interval(rfrom, rto)) =>
        Interval(lfrom min rfrom, lto max rto)
      case (l, r) => if (l == r) l else l.toInterval ⊔ r.toInterval
    }

    // meet operator
    def ⊓(that: Elem): Elem = (this, that) match {
      case (Bot, _) | (_, Top) => this
      case (_, Bot) | (Top, _) => that
      case (Interval(lfrom, lto), Interval(rfrom, rto)) =>
        Interval(lfrom max rfrom, lto min rto).norm
      case (l, r) => if (l == r) l else l.toInterval ⊓ r.toInterval
    }

    // get single value
    def getSingle: Flat[INum] = this match {
      case Bot => FlatBot
      case Single(int) => FlatElem(int)
      case _ => FlatTop
    }

    // contains check
    def contains(target: INum): Boolean = this match {
      case Bot => false
      case Single(int) => int == target
      case Interval(from, to) =>
        val long = target.long
        (from <= long) && (long <= to)
      case _ => true
    }

    def toInterval: Elem = this match {
      case Single(INum(long)) => Interval(long, long)
      case _ => this
    }

    // iterators
    final def iterator: Iterator[INum] = (this match {
      case Bot => Nil
      case Single(int) => List(int)
      case Interval(from, to) => (from to to).toList.map(INum(_))
      case Top => exploded(s"cannot iterate: $this")
    }).iterator

    // normalize
    def norm: Elem = this match {
      case Interval(from, to) => getInterval(from, to)
      case _ => this
    }
  }

  // integer operators
  implicit class ElemOp(elem: Elem) extends IntOp {
    def plus(that: Elem): Elem = (elem, that) match {
      case (Bot, _) | (_, Bot) => Bot
      case (Single(INum(l)), Single(INum(r))) => Single(INum(l + r))
      case (Interval(lfrom, lto), Interval(rfrom, rto)) =>
        Interval(lfrom + rfrom, lto + rto)
      case (l, r) => l.toInterval plus r.toInterval
    }
  }
}
