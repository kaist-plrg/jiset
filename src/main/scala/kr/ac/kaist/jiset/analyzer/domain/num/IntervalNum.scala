package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.analyzer.exploded
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Appender._
import kr.ac.kaist.jiset.util.Useful._

object IntervalNum extends NumDomain {
  import Ordering.Double.IeeeOrdering

  // elements
  case object Bot extends Elem
  case class Single(int: Num) extends Elem
  case class Interval(from: Double, to: Double) extends Elem
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
  def getInterval(from: Double, to: Double): Elem = {
    if (from.isNaN || to.isNaN) Top
    else if (from > to) Bot
    else if (from == to) Single(Num(from))
    else Interval(from, to)
  }

  // abstraction functions
  def apply(elems: Num*): Elem = this(elems)
  def apply(elems: Iterable[Num]): Elem = alpha(elems)
  def alpha(elems: Iterable[Num]): Elem = elems.size match {
    case 0 => Bot
    case 1 => Single(elems.head)
    case _ => {
      val ints = elems.map(_.double)
      Interval(ints.min, ints.max)
    }
  }

  // elements
  sealed trait Elem extends Iterable[Num] with ElemTrait {
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
    def getSingle: Flat[Num] = this match {
      case Bot => FlatBot
      case Single(int) => FlatElem(int)
      case _ => FlatTop
    }

    // contains check
    def contains(target: Num): Boolean = this match {
      case Bot => false
      case Single(int) => int == target
      case Interval(from, to) =>
        val double = target.double
        (from <= double) && (double <= to)
      case _ => true
    }

    def toInterval: Elem = this match {
      case Single(Num(double)) =>
        if (double.isNaN) Top else Interval(double, double)
      case _ => this
    }

    // iterators
    final def iterator: Iterator[Num] = (this match {
      case Bot => Nil
      case Single(int) => List(int)
      case _ => exploded(s"cannot iterate: $this")
    }).iterator

    // normalize
    def norm: Elem = this match {
      case Interval(from, to) => getInterval(from, to)
      case _ => this
    }
  }

  // integer operators
  implicit class ElemOp(elem: Elem) extends NumOp {
    def plus(that: Elem): Elem = aux(_ + _)(elem, that)
    def mul(that: Elem): Elem = aux(_ * _)(elem, that)
    private def aux(op: (Double, Double) => Double): (Elem, Elem) => Elem = {
      def f(left: Elem, right: Elem): Elem = (left, right) match {
        case (Bot, _) | (_, Bot) => Bot
        case (Single(Num(l)), Single(Num(r))) => Single(Num(op(l, r)))
        case (Interval(lfrom, lto), Interval(rfrom, rto)) =>
          val set = for (x <- Set(lfrom, lto); y <- Set(rfrom, rto)) yield op(x, y)
          getInterval(set.min, set.max)
        case (l, r) => f(l.toInterval, r.toInterval)
      }
      f
    }
    def plusInt(that: AbsInt): Elem = auxInt(_ + _)(elem, that.getSingle)
    def mulInt(that: AbsInt): Elem = auxInt(_ * _)(elem, that.getSingle)
    private def auxInt(op: (Double, Long) => Double): (Elem, Flat[INum]) => Elem = {
      case (Bot, _) | (_, FlatBot) => Bot
      case (Single(Num(l)), FlatElem(INum(r))) => Single(Num(op(l, r)))
      case _ => Top
    }
  }
}
