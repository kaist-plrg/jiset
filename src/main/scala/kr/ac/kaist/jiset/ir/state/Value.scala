package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.js.ast._
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.util.Useful._
import scala.collection.mutable.{ Map => MMap }

// values
sealed trait Value extends IRComponent {
  // escape completion
  def escaped(st: State): PureValue = ???
  // this match {
  //   case addr: Addr => completionType(st) match {
  //     case CompletionType.NoCompl => this
  //     case CompletionType.Normal => st(addr, Str("Value"))
  //     case _ => error(s"unchecked abrupt completion: ${addr}")
  //   }
  //   case _ => this
  // }

  // check completion
  def isCompletion(st: State): Boolean = ???
  // completionType(st) match {
  //   case CompletionType.NoCompl => false
  //   case _ => true
  // }

  // check abrupt completion
  def isAbruptCompletion(st: State): Boolean = ???
  // completionType(st) match {
  //   case CompletionType.NoCompl => false
  //   case CompletionType.Normal => false
  //   case _ => true
  // }

  // completion type
  def completionType(st: State): CompletionType = ???
  // this match {
  //   case (addr: Addr) => st(addr) match {
  //     case m @ IRMap(Ty("Completion"), _, _) => CompletionType
  //       .toType(m(Str("Type")))
  //       .getOrElse { error(s"invalid completion record: ${m.beautified}") }
  //     case _ => CompletionType.NoCompl
  //   }
  //   case _ => CompletionType.NoCompl
  // }

  // wrap completion
  def wrapCompletion(
    st: State,
    newTy: CompletionType = CompletionType.Normal
  ): CompValue = ???
  // CompletionType.toAddr(newTy) match {
  //   case Some(newAddr) => this match {
  //     case addr: Addr => st(addr) match {
  //       case m @ IRMap(Ty("Completion"), _, _) => this
  //       case _ => getCompletion(st)(value = this, ty = newAddr)
  //     }
  //     case _ => getCompletion(st)(value = this, ty = newAddr)
  //   }
  //   case None => this
  // }
}

// completions
case class CompValue(
  ty: Const,
  value: PureValue,
  target: Option[String]
) extends Value

// pure values
sealed trait PureValue extends Value

// constants
case class Const(name: String) extends PureValue

// addresses
sealed trait Addr extends PureValue
case class NamedAddr(name: String) extends Addr
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
