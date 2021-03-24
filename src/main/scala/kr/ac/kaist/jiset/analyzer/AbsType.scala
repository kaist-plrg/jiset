package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.util.Useful._
import scala.annotation.tailrec

// abstract states
case class AbsType private (
  val set: Set[Type]
) {
  // bottom check
  def isBottom: Boolean = set.isEmpty

  // partial order
  def ⊑(that: AbsType): Boolean = (
    this.set.subsetOf(that.set) ||
    this.set.forall(_.ancestors.exists(that.set contains _))
  )

  // not partial order
  def !⊑(that: AbsType): Boolean = !(this ⊑ that)

  // join operator
  def ⊔(that: AbsType): AbsType = (new AbsType(this.set ++ that.set)).norm

  // conversion to normal completion
  def toComp: AbsType = (new AbsType(set.map(_.toComp: Type))).norm

  // escape completions
  def escaped: AbsType = (new AbsType(set.flatMap(_.escaped: Option[Type]))).norm
  def escapedSet: Set[PureType] = set.flatMap(_.escaped)

  // closure set
  def fidSet: Set[Int] = set.collect { case CloT(fid) => fid }

  // absent check
  def isMustAbsent: Boolean = set == Set(Absent)
  def isAbsent: Set[Boolean] = set.map(_ == Absent)

  // remove types
  def -(t: Type): AbsType = new AbsType(set - t)
  def --(ts: Iterable[Type]): AbsType = new AbsType(set -- ts)

  // normalize types
  private def norm: AbsType = new AbsType(normalizedSet)
  private def normalizedSet: Set[Type] = {
    var set = this.set
    @tailrec
    def aux(pairs: List[(Set[Type], Type)]): Unit = pairs match {
      case (from, to) :: remain if from subsetOf set =>
        set --= from
        set += to
      case _ :: remain if set.size >= 2 => aux(remain)
      case _ =>
    }
    aux(Type.mergedPairs)
    set
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
  val Bot: AbsType = new AbsType(Set())

  // constructor
  def apply(set: Set[Type]): AbsType = set.size match {
    case 0 => Bot
    case 1 => new AbsType(set)
    case _ => (new AbsType(set)).norm
  }
  def apply(seq: Type*): AbsType = AbsType(seq.toSet)
}
