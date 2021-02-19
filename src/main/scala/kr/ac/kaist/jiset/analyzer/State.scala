package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.cfg.Function

// states
case class State(ctxt: Ctxt, heap: Heap)

// contexts
case class Ctxt(globals: Env, locals: Env, retVal: Value)

// environments
case class Env(map: Map[String, Value])

// heaps
case class Heap(map: Map[Addr, Obj])

// objects
sealed trait Obj
case class SymbolObj(desc: Str) extends Obj
case class MapObj(props: Map[String, Value]) extends Obj
case class ListObj(values: List[Value]) extends Obj

// values
sealed trait Value

// addresses
sealed trait Addr extends Value
case class NamedAddr(name: String) extends Addr
case class DynamicAddr(long: Long) extends Addr

// functions
case class Clo(func: Function, env: Env) extends Value

// TODO continuations
case class Cont() extends Value

// abstract syntax trees
case class ASTVal(name: String) extends Value

// primitive values
sealed trait Prim extends Value
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
