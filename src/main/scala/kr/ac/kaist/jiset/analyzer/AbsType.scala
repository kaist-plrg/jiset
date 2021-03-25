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
  def ⊔(that: AbsType): AbsType = new AbsType(this.set ++ that.set).norm

  // meet operator
  def ⊓(that: AbsType): AbsType = if (this == that) this else new AbsType((
    this.set.filter(_.ancestors.exists(that.set contains _)) ++
    that.set.filter(_.ancestors.exists(this.set contains _))
  ))

  // conversion to normal completion
  def toComp: AbsType = new AbsType(set.map(_.toComp: Type)).norm

  // escape completions
  def escaped: AbsType = new AbsType(set.flatMap(_.escaped: Option[Type])).norm
  def escapedSet: Set[PureType] = set.flatMap(_.escaped)

  // closure set
  def fidSet: Set[Int] = set.collect { case CloT(fid) => fid }

  // absent check
  def isMustAbsent: Boolean = set == Set(Absent)
  def isAbsent: AbsType = {
    val bs = set.map(_ == Absent)
    bs.size match {
      case 0 => AbsType.Bot
      case 1 => Bool(bs.head).abs
      case _ => BoolT.abs
    }
  }

  // get boolean set
  def bool: Set[Boolean] =
    if (set contains BoolT) Set(true, false)
    else if (set contains T) Set(true)
    else if (set contains F) Set(false)
    else Set()

  // normalize types
  private def norm: AbsType = new AbsType(normalizedSet)
  private def normalizedSet: Set[Type] = {
    var set = this.set.filter(!_.strictAncestors.exists(this.set contains _))
    @tailrec
    def aux(pairs: List[(Type, Set[Type])]): Unit = pairs match {
      case (to, from) :: remain if from subsetOf set =>
        set --= from
        set += to
      case _ :: remain if set.size >= 2 => aux(remain)
      case _ =>
    }
    aux(Type.typeAlias)
    set
  }

  // equality between abstract types
  def =^=(that: AbsType): AbsType = (this.set.toList, that.set.toList) match {
    case (Nil, Nil) => AbsType.Bot
    case (List(l: SingleT), List(r: SingleT)) => Bool(l == r).abs
    case _ if (this ⊓ that).isBottom => AF
    case _ => BoolT.abs
  }

  // abstract numeric negation
  def unary_-(): AbsType = new AbsType(set.collect {
    case NumericT => NumericT
    case NumT => NumT
    case BigIntT => BigIntT
    case Num(n) => Num(-n)
    case BigInt(n) => BigInt(-n)
  })

  // remove types
  def -(z: Type): AbsType = new AbsType(for {
    x <- set
    y <- x.bases
    t <- y - z
  } yield t).norm

  // abstract boolean negation
  def unary_!(): AbsType = new AbsType(bool.map(b => Bool(!b))).norm

  // abstract boolean binary operations
  def &&(that: AbsType): AbsType = boolBOp(this, that, _ && _)
  def ||(that: AbsType): AbsType = boolBOp(this, that, _ || _)
  def ^(that: AbsType): AbsType = boolBOp(this, that, _ ^ _)
  private def boolBOp(
    left: AbsType,
    right: AbsType,
    bop: (Boolean, Boolean) => Boolean
  ): AbsType = new AbsType(for {
    l <- left.bool
    r <- right.bool
  } yield bop(l, r)).norm

  // conversion to string
  override def toString: String = {
    if (set.size == 0) "⊥"
    else if (set.size == 1) set.head.toString
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
    case _ => new AbsType(set).norm
  }
  def apply(seq: Type*): AbsType = AbsType(seq.toSet)
}
