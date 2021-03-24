package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.ir._

sealed trait Type {
  // conversion to abstract type
  def abs: AbsType = AbsType(this)

  // conversion to
  def toComp: CompType = this match {
    case (t: PureType) => NormalT(t)
    case (t: CompType) => t
  }

  // conversion to string
  override def toString: String = this match {
    case NameT(name) => s"$name"
    case AstT(name) => s"☊($name)"
    case ConstT(name) => s"~$name~"
    case CloT(fid) => s"λ[$fid]"
    case NumT => "num"
    case BigIntT => "bigint"
    case StrT => "str"
    case BoolT => "bool"
    case NilT => s"[]"
    case ListT(elem) => s"[$elem]"
    case SymbolT => "symbol"
    case NormalT(t) => s"Normal($t)"
    case AbruptT => s"Abrupt"
    case Num(n) => s"$n"
    case BigInt(b) => s"${b}n"
    case Str(str) => "\"" + str + "\""
    case Bool(b) => s"$b"
    case Undef => "undef"
    case Null => "null"
    case Absent => "?"
  }
}

// completion types
sealed trait CompType extends Type
case class NormalT(value: PureType) extends CompType
case object AbruptT extends CompType

// pure types
sealed trait PureType extends Type

// norminal types
case class NameT(name: String) extends PureType

// AST types
case class AstT(name: String) extends PureType

// constant types
case class ConstT(name: String) extends PureType

// closure types
case class CloT(fid: Int) extends PureType

// list types
case object NilT extends PureType
case class ListT(elem: PureType) extends PureType

// symbol types
case object SymbolT extends PureType

// primitive types
case object NumT extends PureType
case object BigIntT extends PureType
case object StrT extends PureType
case object BoolT extends PureType

// single concrete type
sealed trait SingleT extends PureType
case class Num(double: Double) extends SingleT {
  override def equals(that: Any): Boolean = that match {
    case that: Num => doubleEquals(this.double, that.double)
    case _ => false
  }
}
case class BigInt(bigint: scala.BigInt) extends SingleT
case class Str(str: String) extends SingleT
case class Bool(bool: Boolean) extends SingleT
case object Undef extends SingleT
case object Null extends SingleT
case object Absent extends SingleT
