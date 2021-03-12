package kr.ac.kaist.jiset.analyzer

sealed trait Type {
  // conversion to string
  override def toString: String = this match {
    case NameT(name) => s"$name"
    case AstT(name) => s"☊($name)"
    case ConstT(name) => s"~$name~"
    case CloT(fid) => s"λ[$fid]"
    case NumT => "num"
    case INumT => "int"
    case BigINumT => "bigint"
    case StrT => "str"
    case BoolT => "bool"
    case UndefT => "undef"
    case NullT => "null"
    case AbsentT => "?"
    case ListT => "list"
    case SymbolT => "symbol"
    case NormalT(t) => s"Normal($t)"
    case AbruptT => s"Abrupt"
  }
}

// completion types
case class NormalT(value: PureType) extends Type
case object AbruptT extends Type

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
case object ListT extends PureType

// symbol types
case object SymbolT extends PureType

// primitive types
case object NumT extends PureType
case object INumT extends PureType
case object BigINumT extends PureType
case object StrT extends PureType
case object BoolT extends PureType
case object UndefT extends PureType
case object NullT extends PureType
case object AbsentT extends PureType
