package kr.ac.kaist.jiset.analyzer

trait Type {
  // conversion to string
  override def toString: String = this match {
    case AstT(name) => s"☊($name)"
    case NumT => "num"
    case INumT => "int"
    case BigINumT => "bigint"
    case StrT => "str"
    case BoolT => "bool"
    case UndefT => "undef"
    case NullT => "null"
    case AbsentT => "?"
  }
}

// AST types
case class AstT(name: String) extends Type

// primitive types
case object NumT extends Type
case object INumT extends Type
case object BigINumT extends Type
case object StrT extends Type
case object BoolT extends Type
case object UndefT extends Type
case object NullT extends Type
case object AbsentT extends Type
