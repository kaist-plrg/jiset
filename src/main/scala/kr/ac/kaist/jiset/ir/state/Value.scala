package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.cfg.Function

// values
sealed trait Value

// completion
case class Completion(ty: CompletionType, value: PureValue, target: PureValue) extends Value
sealed abstract class CompletionType(name: String) {
  // conversion to string
  override def toString: String = name

  // conversion to short string
  def shortString: String = name.substring(0, 1).toUpperCase
}
object CompletionType {
  val all: List[CompletionType] = List(
    CompNormal, CompThrow, CompContinue, CompBreak, CompReturn
  )
  val tyMap: Map[String, CompletionType] = all.map(t => t.toString -> t).toMap
}
case object CompNormal extends CompletionType("normal")
case object CompThrow extends CompletionType("throw")
case object CompContinue extends CompletionType("continue")
case object CompBreak extends CompletionType("break")
case object CompReturn extends CompletionType("return")

// pure values
sealed trait PureValue extends Value

// locations
sealed trait Loc extends PureValue {
  override def toString: String = this match {
    case NamedAddr(name) => name
    case AllocSite(fid, k) => s"$fid:$k"
    case CallSite(fid, k, ty) => s"$fid:$k:$ty"
  }

  def getOrder: (Int, Int, Int, String) = this match {
    case NamedAddr(name) => (0, 0, 0, name)
    case AllocSite(fid, k) => (1, fid, k, "")
    case CallSite(fid, k, ty) => (2, fid, k, ty.toString)
  }
}
case class AllocSite(fid: Int, asite: Int) extends Loc
case class CallSite(fid: Int, csite: Int, ty: LocType) extends Loc

// TODO location types
case class LocType(name: String) {
  override def toString: String = name
}

// addresses
sealed trait Addr extends PureValue {
  def toLoc: Loc = this match {
    case (x: NamedAddr) => x
    case (_: DynamicAddr) => ???
  }
}
case class NamedAddr(name: String) extends Addr with Loc
case class DynamicAddr(k: Int) extends Addr

// constants
case class Const(const: String) extends PureValue

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
