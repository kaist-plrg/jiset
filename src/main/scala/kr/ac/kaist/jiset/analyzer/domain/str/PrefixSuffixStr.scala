package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.analyzer.exploded
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Appender._
import kr.ac.kaist.jiset.util.Useful._
import scala.annotation.tailrec

object PrefixSuffixStr extends StrDomain {
  // elements
  case object Bot extends Elem
  case class Single(str: Str) extends Elem
  case class PrefixSuffix(prefix: String, suffix: String) extends Elem
  lazy val Top = PrefixSuffix("", "")

  // appender
  implicit val app: App[Elem] = (app, elem) => app >> (elem match {
    case Bot => "⊥"
    case Top => "str"
    case Single(elem) => elem.toString
    case PrefixSuffix(prefix, suffix) =>
      s"<$prefix*, *$suffix>"
  })

  // abstraction functions
  def apply(elems: Str*): Elem = this(elems)
  def apply(elems: Iterable[Str]): Elem = alpha(elems)
  def alpha(elems: Iterable[Str]): Elem = elems.size match {
    case 0 => Bot
    case 1 => Single(elems.head)
    case _ => {
      val strs = elems.map(_.str)
      val prefix = strs.reduce(lcp)
      val suffix = strs.reduce(lcs)
      PrefixSuffix(prefix, suffix)
    }
  }

  // the longest common prefix/suffix
  @tailrec
  private def aux(
    l: List[Char],
    r: List[Char],
    s: List[Char]
  ): List[Char] = (l, r) match {
    case (lhd :: ltl, rhd :: rtl) if lhd == rhd => aux(ltl, rtl, lhd :: s)
    case _ => s.reverse
  }
  def lcp(l: String, r: String): String =
    aux(l.toList, r.toList, Nil).mkString
  def lcs(l: String, r: String): String =
    aux(l.toList.reverse, r.toList.reverse, Nil).reverse.mkString

  // elements
  sealed trait Elem extends Iterable[Str] with ElemTrait {
    // partial order
    def ⊑(that: Elem): Boolean = (this, that) match {
      case (Bot, _) => true
      case (_, Bot) => false
      case (PrefixSuffix(lprefix, lsuffix), PrefixSuffix(rprefix, rsuffix)) =>
        (lprefix startsWith rprefix) && (lsuffix endsWith rsuffix)
      case (l, r) => l.toPrefixSuffix ⊑ r.toPrefixSuffix
    }

    // join operator
    def ⊔(that: Elem): Elem = (this, that) match {
      case (Bot, _) | (_, Top) => that
      case (_, Bot) | (Top, _) => this
      case (PrefixSuffix(lprefix, lsuffix), PrefixSuffix(rprefix, rsuffix)) =>
        PrefixSuffix(lcp(lprefix, rprefix), lcs(lsuffix, rsuffix))
      case (l, r) => if (l == r) l else l.toPrefixSuffix ⊔ r.toPrefixSuffix
    }

    // meet operator
    def ⊓(that: Elem): Elem = (this, that) match {
      case (Bot, _) | (_, Top) => this
      case (_, Bot) | (Top, _) => that
      case (l, r) if l ⊑ r => l
      case (l, r) if r ⊑ l => r
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
      case PrefixSuffix(prefix, suffix) =>
        val str = target.str
        (str startsWith prefix) && (str endsWith suffix)
    }

    def toPrefixSuffix: Elem = this match {
      case Single(Str(str)) => PrefixSuffix(str, str)
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
      case (Single(Str(l)), Single(Str(r))) => Single(Str(l + r))
      case (PrefixSuffix(lprefix, _), PrefixSuffix(_, rsuffix)) =>
        PrefixSuffix(lprefix, rsuffix)
      case (Single(Str(l)), PrefixSuffix(rprefix, rsuffix)) =>
        PrefixSuffix(l + rprefix, rsuffix)
      case (PrefixSuffix(lprefix, lsuffix), Single(Str(r))) =>
        PrefixSuffix(lprefix, lsuffix + r)
      case (l, r) => l.toPrefixSuffix plus r.toPrefixSuffix
    }
    def plusNum(that: AbsNum): Elem = (elem, that.getSingle) match {
      case (Bot, _) | (_, FlatBot) => Bot
      case (Top, _) | (_, FlatTop) => Top
      case (Single(Str(l)), FlatElem(Num(r))) =>
        Single(Str(l + Character.toChars(r.toInt).mkString("")))
      case (PrefixSuffix(prefix, suffix), FlatElem(Num(r))) =>
        PrefixSuffix(prefix, suffix + Character.toChars(r.toInt).mkString(""))
    }
  }
}
