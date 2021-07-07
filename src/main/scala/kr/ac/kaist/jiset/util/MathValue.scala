package kr.ac.kaist.jiset.util

sealed trait MathValue {
  // minimum value
  def min(that: MathValue): MathValue = (this, that) match {
    case (_, MVPosInf) => this
    case (MVPosInf, _) => that
    case (MVNegInf, _) | (_, MVNegInf) => MVNegInf
    case (MVFinite(l), MVFinite(r)) => MVFinite(l min r)
  }

  // maximum value
  def max(that: MathValue): MathValue = (this, that) match {
    case (_, MVNegInf) => this
    case (MVNegInf, _) => that
    case (MVPosInf, _) | (_, MVPosInf) => MVPosInf
    case (MVFinite(l), MVFinite(r)) => MVFinite(l max r)
  }

  // conversion to long
  def toLong: Option[Long] = this match {
    case MVFinite(d) if d.toLong == d => Some(d.toLong)
    case _ => None
  }

  // conversion to double
  def toDouble: Double = this match {
    case MVFinite(d) => d.toDouble
    case MVPosInf => Double.PositiveInfinity
    case MVNegInf => Double.NegativeInfinity
  }

  // conversion to bigint
  def toBigInt: Option[BigInt] = this match {
    case MVFinite(d) if d.toBigInt == d => Some(d.toBigInt)
    case _ => None
  }
}
object MathValue {
  // constructors
  def apply(double: Double) = {
    if (double.isPosInfinity) MVPosInf
    else if (double.isNegInfinity) MVNegInf
    else MVFinite(double)
  }
  def apply(long: Long) = MVFinite(long)
  def apply(bigint: BigInt) = MVFinite(BigDecimal(bigint))
}
case class MVFinite(d: BigDecimal) extends MathValue
case object MVPosInf extends MathValue
case object MVNegInf extends MathValue
