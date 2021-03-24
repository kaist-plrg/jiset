package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.util.Useful._

// abstract states
case class AbsType(
  set: Set[Type] = Set()
) {
  // bottom check
  def isBottom: Boolean = set.isEmpty

  // partial order
  def ⊑(that: AbsType): Boolean = this.set subsetOf that.set

  // not partial order
  def !⊑(that: AbsType): Boolean = !(this ⊑ that)

  // join operator
  def ⊔(that: AbsType): AbsType = AbsType(this.set ++ that.set)

  // meet operator
  def ⊓(that: AbsType): AbsType = AbsType(this.set intersect that.set)

  // conversion to normal completion
  def toComp: AbsType = AbsType(set.map(_.toComp: Type))

  // escape completions
  def escaped: AbsType = AbsType(set.flatMap(_.escaped: Option[Type]))
  def escapedSet: Set[PureType] = set.flatMap(_.escaped)

  // absent check
  def isMustAbsent: Boolean = set == Set(Absent)
  def isAbsent: Set[Boolean] = set.map(_ == Absent)

  // remove types
  def -(t: Type): AbsType = AbsType(set - t)
  def --(ts: Iterable[Type]): AbsType = AbsType(set -- ts)

  // merge for calling functions
  def mergeForCall: Set[PureType] = (escapedSet - Absent).map {
    case (t: SingleT) => t.upcast
    case ListT(t: SingleT) => ListT(t.upcast)
    case t => t
  }

  // conversion to string
  override def toString: String = {
    if (set.size == 0) "⊥"
    if (set.size == 1) set.head.toString
    else set.mkString("(", " | ", ")")
  }
}
object AbsType {
  // bottom value
  val Bot: AbsType = AbsType()

  // constructor
  def apply(seq: Type*): AbsType = AbsType(seq.toSet)
}
