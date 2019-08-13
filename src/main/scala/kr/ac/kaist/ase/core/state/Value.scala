package kr.ac.kaist.ase.core

import kr.ac.kaist.ase.AST

// CORE Values
sealed trait Value extends CoreNode

// CORE Addresses
sealed trait Addr extends Value
case class NamedAddr(name: String) extends Addr
case class DynamicAddr(long: Long) extends Addr

// CORE Functions
case class Func(name: String, params: List[Id], varparam: Option[Id], body: Inst) extends Value {
  override def toString: String = s"""Func("$name", $params, $varparam, $body)"""
}

case class Cont(params: List[Id], body: Inst, context: Context, ctxStack: List[Context]) extends Value
// CORE Constants
sealed trait Const extends Value
case class Num(double: Double) extends Const {
  override def equals(that: Any): Boolean = that match {
    case that: Num => doubleEquals(this.double, that.double)
    case _ => false
  }
}
case class ASTVal(ast: AST) extends Value
case class ASTMethod(func: Func, env: Map[Id, Value]) extends Value
case class INum(long: Long) extends Const
case class Str(str: String) extends Const
case class Bool(bool: Boolean) extends Const
case object Undef extends Const
case object Null extends Const
case object Absent extends Const
