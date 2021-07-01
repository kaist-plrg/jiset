package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.js.ast._
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.util.Useful._
import scala.collection.mutable.{ Map => MMap }

// IR Values
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

// IR Addresses
sealed trait Addr extends Value
case class NamedAddr(name: String) extends Addr
case class DynamicAddr(long: Long) extends Addr

// IR Functions
case class Func(algo: Algo) extends Value

// IR Closures
case class Clo(
  ctxtName: String,
  params: List[Id],
  locals: MMap[Id, Value],
  body: Inst
) extends Value {
  // get name
  def name: String = ctxtName + ":closure"
}

// IR Continuations
case class Cont(params: List[Id], body: Inst, context: Context, ctxtStack: List[Context]) extends Value

// IR Constants
sealed trait Const extends Value

// IR Numeric
sealed trait Numeric extends Const {
  // conversion to big decimal
  def toBigDecimal: BigDecimal = this match {
    case Num(double) => BigDecimal(double)
    case INum(long) => BigDecimal(long)
    case BigINum(bigint) => BigDecimal(bigint)
  }
}

case class Num(double: Double) extends Numeric {
  override def equals(that: Any): Boolean = that match {
    case that: Num => doubleEquals(this.double, that.double)
    case _ => false
  }
}
case class ASTVal(ast: AST) extends Value
case class INum(long: Long) extends Numeric
case class BigINum(b: BigInt) extends Numeric
case class Str(str: String) extends Const
case class Bool(bool: Boolean) extends Const
case object Undef extends Const
case object Null extends Const
case object Absent extends Const
