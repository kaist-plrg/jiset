package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.cfg.Function

// values
sealed trait Value

// completion
case class Completion(ty: String, value: PureValue, target: PureValue) extends Value

// pure values
sealed trait PureValue extends Value

// addresses
sealed trait Addr extends PureValue
case class NamedAddr(name: String) extends Addr
case class DynamicAddr(long: Long) extends Addr

// functions
case class Clo(fid: Int, env: Env = Env()) extends PureValue

// TODO continuations
case class Cont() extends PureValue

// abstract syntax trees
case class ASTVal(name: String) extends PureValue

// primitive values
sealed trait Prim extends PureValue
case class Num(double: Double) extends Prim {
  override def equals(that: Any): Boolean = that match {
    case that: Num => doubleEquals(this.double, that.double)
    case _ => false
  }
}
case class INum(long: Long) extends Prim
case class BigINum(bigint: BigInt) extends Prim
case class Str(str: String) extends Prim
case class Bool(bool: Boolean) extends Prim
case object Undef extends Prim
case object Null extends Prim
case object Absent extends Prim
