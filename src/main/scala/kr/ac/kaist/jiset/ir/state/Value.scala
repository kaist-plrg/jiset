package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.js.ast._
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.util.Useful._
import scala.collection.mutable.{ Map => MMap }

// values
sealed trait Value extends IRComponent {
  // escape completion
  def escaped: PureValue = this match {
    case CompValue(CONST_NORMAL, value, _) => value
    case CompValue(_, _, _) =>
      error(s"unchecked abrupt completion: ${this.beautified}")
    case pure: PureValue => pure
  }

  // check abrupt completion
  def isCompletion: Boolean = this match {
    case comp: CompValue => true
    case _ => false
  }

  // check abrupt completion
  def isAbruptCompletion: Boolean = this match {
    case comp: CompValue => comp.ty != CONST_NORMAL
    case _ => false
  }

  // wrap completion
  def wrapCompletion: CompValue = wrapCompletion(CONST_NORMAL)
  def wrapCompletion(ty: Const): CompValue = this match {
    case comp: CompValue => comp
    case pure: PureValue => CompValue(ty, pure, None)
  }
}

// completions
case class CompValue(
  ty: Const,
  value: PureValue,
  targetOpt: Option[String]
) extends Value {
  def target: Value = targetOpt.fold[Value](CONST_EMPTY)(Str)
}

// pure values
sealed trait PureValue extends Value

// constants
case class Const(name: String) extends PureValue

// addresses
sealed trait Addr extends PureValue
case class NamedAddr(name: String) extends Addr {
  // TODO REMOVE!!
  if (name startsWith ("CONST_")) error(s"invalid address name: $name")
}
case class DynamicAddr(long: Long) extends Addr

// functions
case class Func(algo: Algo) extends PureValue

// closures
case class Clo(
  ctxtName: String,
  params: List[Id],
  locals: MMap[Id, Value],
  cursorOpt: Option[Cursor]
) extends PureValue {
  // get name
  def name: String = ctxtName + ":closure"
}

// continuations
case class Cont(
  params: List[Id],
  context: Context,
  ctxtStack: List[Context]
) extends PureValue

// AST values
case class ASTVal(ast: AST) extends PureValue

// simple values
sealed trait SimpleValue extends PureValue

// numeric values
sealed trait Numeric extends SimpleValue {
  // conversion to big decimal
  def toMathValue: MathValue = this match {
    case Num(double) => MathValue(double)
    case INum(long) => MathValue(long)
    case BigINum(bigint) => MathValue(bigint)
  }
}

// floating-point number values
case class Num(double: Double) extends Numeric {
  override def equals(that: Any): Boolean = that match {
    case that: Num => doubleEquals(this.double, that.double)
    case _ => false
  }
}

// integers
case class INum(long: Long) extends Numeric

// big integers
case class BigINum(b: BigInt) extends Numeric

// strings
case class Str(str: String) extends SimpleValue

// booleans
case class Bool(bool: Boolean) extends SimpleValue

// undefined
case object Undef extends SimpleValue

// null
case object Null extends SimpleValue

// absent
case object Absent extends SimpleValue
