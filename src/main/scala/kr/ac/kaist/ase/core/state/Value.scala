package kr.ac.kaist.ase.core

import kr.ac.kaist.ase.model.AST

// CORE Values
sealed trait Value extends CoreNode

// CORE Addresses
sealed trait Addr extends Value
case class NamedAddr(name: String) extends Addr
case class DynamicAddr(long: Long) extends Addr

// CORE Functions
case class Func(params: List[Id], body: Inst) extends Value

// CORE Constants
sealed trait Const extends Value
case class Num(double: Double) extends Const {
  override def equals(that: Any): Boolean = that match {
    case that: Num => doubleEquals(this.double, that.double)
    case _ => false
  }
}
case class ASTVal(ast: AST) extends Value
case class INum(long: Long) extends Const
case class Str(str: String) extends Const
case class Bool(bool: Boolean) extends Const
case object Undef extends Const
case object Null extends Const
