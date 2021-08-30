package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.analyzer.exploded
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Appender._
import kr.ac.kaist.jiset.util.Useful._

object CharIncStr extends StrDomain {
  // elements
  case object Bot extends Elem
  case class Single(str: Str) extends Elem
  case class CharInc(lower: Set[Char], upper: Set[Char]) extends Elem
  case object Top extends Elem

  // appender
  implicit val app: App[Elem] = (app, elem) => app >> (elem match {
    case Bot => "⊥"
    case Single(elem) => elem.toString
    case CharInc(lower, upper) =>
      s"<[${lower.toList.sorted.mkString}], [${upper.toList.sorted.mkString}]>"
    case Top => "str"
  })

  // abstraction functions
  def apply(elems: Str*): Elem = this(elems)
  def apply(elems: Iterable[Str]): Elem = alpha(elems)
  def alpha(elems: Iterable[Str]): Elem = elems.size match {
    case 0 => Bot
    case 1 => Single(elems.head)
    case _ => {
      val sets = elems.map(_.str.toSet)
      val lower = sets.reduce(_ intersect _)
      val upper = sets.reduce(_ ++ _)
      CharInc(lower, upper)
    }
  }

  // elements
  sealed trait Elem extends Iterable[Str] with ElemTrait {
    // partial order
    def ⊑(that: Elem): Boolean = (this, that) match {
      case (Bot, _) | (_, Top) => true
      case (_, Bot) | (Top, _) => false
      case (Single(Str(l)), Single(Str(r))) => l == r
      case (Single(l), r: CharInc) => r contains l
      case (_: CharInc, _: Single) => false
      case (CharInc(llower, lupper), CharInc(rlower, rupper)) =>
        (rlower subsetOf llower) && (lupper subsetOf rupper)
    }

    // join operator
    def ⊔(that: Elem): Elem = (this, that) match {
      case (Bot, _) | (_, Top) => that
      case (_, Bot) | (Top, _) => this
      case (CharInc(llower, lupper), CharInc(rlower, rupper)) =>
        CharInc(llower intersect rlower, lupper ++ rupper)
      case (l, r) => if (l == r) l else l.toCharInc ⊔ r.toCharInc
    }

    // meet operator
    def ⊓(that: Elem): Elem = (this, that) match {
      case (Bot, _) | (_, Top) => this
      case (_, Bot) | (Top, _) => that
      case (l, r) if l ⊑ r => l
      case (l, r) if r ⊑ l => r
      case (CharInc(llower, lupper), CharInc(rlower, rupper)) =>
        CharInc(llower ++ rlower, lupper intersect rupper)
      case _ => Bot
    }

    // get single value
    def getSingle: Flat[Str] = this match {
      case Bot => FlatBot
      case Single(str) => FlatElem(str)
      case _ => FlatTop
    }

    // contains check
    def contains(target: Str): Boolean = this match {
      case Bot => false
      case Single(str) => str == target
      case CharInc(lower, upper) =>
        val set = target.str.toSet
        (lower subsetOf set) && (set subsetOf upper)
      case Top => true
    }

    def toCharInc: Elem = this match {
      case Single(Str(str)) =>
        val set = str.toSet
        CharInc(set, set)
      case _ => this
    }

    // iterators
    final def iterator: Iterator[Str] = (this match {
      case Bot => None
      case Single(str) => Some(str)
      case _ => exploded(s"cannot iterate: $this")
    }).iterator
  }

  // string operators
  implicit class ElemOp(elem: Elem) extends StrOp {
    def plus(that: Elem): Elem = (elem, that) match {
      case (Bot, _) | (_, Bot) => Bot
      case (Top, _) | (_, Top) => Top
      case (Single(Str(l)), Single(Str(r))) => Single(Str(l + r))
      case (CharInc(llower, lupper), CharInc(rlower, rupper)) =>
        CharInc(llower ++ rlower, lupper ++ rupper)
      case (l, r) => l.toCharInc plus r.toCharInc
    }
    def plusNum(that: AbsNum): Elem = (elem, that.getSingle) match {
      case (Bot, _) | (_, FlatBot) => Bot
      case (Top, _) | (_, FlatTop) => Top
      case (Single(Str(l)), FlatElem(Num(r))) =>
        Single(Str(l + Character.toChars(r.toInt).mkString("")))
      case (CharInc(lower, upper), FlatElem(Num(r))) =>
        val set = Character.toChars(r.toInt)
        CharInc(lower ++ set, upper ++ set)
    }
  }
}
