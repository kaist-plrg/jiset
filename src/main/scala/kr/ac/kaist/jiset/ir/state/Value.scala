package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.js.ast._
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.util.Useful._
import scala.collection.mutable.{ Map => MMap }

// values
sealed trait Value extends IRNode {
  // escape completion
  def escaped(st: State): Value = this match {
    case addr: Addr => completionType(st) match {
      case CompletionType.NoCompl => this
      case CompletionType.Normal => st(addr, Str("Value"))
      case _ => error(s"unchecked abrupt completion: ${addr}")
    }
    case _ => this
  }

  // check completion
  def isCompletion(st: State): Boolean = completionType(st) match {
    case CompletionType.NoCompl => false
    case _ => true
  }

  // check abrupt completion
  def isAbruptCompletion(st: State): Boolean = completionType(st) match {
    case CompletionType.NoCompl => false
    case CompletionType.Normal => false
    case _ => true
  }

  // completion type
  def completionType(st: State): CompletionType = this match {
    case (addr: Addr) => st(addr) match {
      case m @ IRMap(Ty("Completion"), _, _) => CompletionType
        .toType(m(Str("Type")))
        .getOrElse { error(s"invalid completion record: ${m.beautified}") }
      case _ => CompletionType.NoCompl
    }
    case _ => CompletionType.NoCompl
  }

  // wrap completion
  def wrapCompletion(
    st: State,
    newTy: CompletionType = CompletionType.Normal
  ): Value = CompletionType.toAddr(newTy) match {
    case Some(newAddr) => this match {
      case addr: Addr => st(addr) match {
        case m @ IRMap(Ty("Completion"), _, _) => this
        case _ => getCompletion(st)(value = this, ty = newAddr)
      }
      case _ => getCompletion(st)(value = this, ty = newAddr)
    }
    case None => this
  }
}

// addresses
sealed trait Addr extends Value
case class NamedAddr(name: String) extends Addr
case class DynamicAddr(long: Long) extends Addr

// functions
case class Func(algo: Algo) extends Value

// closures
case class Clo(
  ctxtName: String,
  params: List[Id],
  locals: MMap[Id, Value],
  body: Inst
) extends Value {
  // get name
  def name: String = ctxtName + ":closure"
}

// continuations
case class Cont(
  params: List[Id],
  body: Inst,
  context: Context,
  ctxtStack: List[Context]
) extends Value

// AST values
case class ASTVal(ast: AST) extends Value

// numeric values
sealed trait Numeric extends Value {
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
case class Str(str: String) extends Value

// booleans
case class Bool(bool: Boolean) extends Value

// undefined
case object Undef extends Value

// null
case object Null extends Value

// absent
case object Absent extends Value
