package kr.ac.kaist.jiset.js

import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.ir._
import scala.collection.mutable.{ Map => MMap }

object BaseModel {
  lazy val globals: Map[String, Value] = Map(
    ALGORITHM -> NamedAddr(ALGORITHM),
    CONTEXT -> Null,
    EXECUTION_STACK -> NamedAddr(EXECUTION_STACK),
    GLOBAL -> NamedAddr(GLOBAL),
    HOST_DEFINED -> Undef,
    INTRINSICS -> NamedAddr(INTRINSICS),
    JOB_QUEUE -> NamedAddr(JOB_QUEUE),
    PRIMITIVE -> NamedAddr(PRIMITIVE),
    REALM -> NamedAddr(REALM),
    RET_CONT -> Undef,
    SYMBOL_REGISTRY -> NamedAddr(SYMBOL_REGISTRY),
    "Number" -> Str("Number"),
    "BigInt" -> Str("BigInt"),
    "Undefined" -> Str("Undefined"),
    "Null" -> Str("Null"),
    "String" -> Str("String"),
    "Boolean" -> Str("Boolean"),
    "Symbol" -> Str("Symbol"),
    "Reference" -> Str("Reference"),
    "Object" -> Str("Object"),
  )

  lazy val heap: Map[Addr, Obj] = builtin.Heap.map ++ Map(
    NamedAddr(ALGORITHM) -> IRMap(ALGORITHM)(Nil),
    NamedAddr(INTRINSICS) -> IRMap(INTRINSICS)(
      for (i <- intrinsics) yield Str(INTRINSIC_PREFIX + i) -> intrinsicToAddr(i)
    ),
    NamedAddr(EXECUTION_STACK) -> IRList(),
    NamedAddr(JOB_QUEUE) -> IRList(),
    NamedAddr(PRIMITIVE) -> IRMap(PRIMITIVE)(List(
      Str("Number") -> NamedAddr(PRIMITIVE + ".Number"),
      Str("BigInt") -> NamedAddr(PRIMITIVE + ".BigInt"),
    )),
    NamedAddr(PRIMITIVE + ".Number") -> IRMap("Record")(List(
      Str("unit") -> Num(1),
      Str("unaryMinus") -> Func(algos("Number::unaryMinus")),
      Str("bitwiseNOT") -> Func(algos("Number::bitwiseNOT")),
      Str("exponentiate") -> Func(algos("Number::exponentiate")),
      Str("multiply") -> Func(algos("Number::multiply")),
      Str("divide") -> Func(algos("Number::divide")),
      Str("remainder") -> Func(algos("Number::remainder")),
      Str("add") -> Func(algos("Number::add")),
      Str("subtract") -> Func(algos("Number::subtract")),
      Str("leftShift") -> Func(algos("Number::leftShift")),
      Str("signedRightShift") -> Func(algos("Number::signedRightShift")),
      Str("unsignedRightShift") -> Func(algos("Number::unsignedRightShift")),
      Str("lessThan") -> Func(algos("Number::lessThan")),
      Str("equal") -> Func(algos("Number::equal")),
      Str("sameValue") -> Func(algos("Number::sameValue")),
      Str("sameValueZero") -> Func(algos("Number::sameValueZero")),
      Str("bitwiseAND") -> Func(algos("Number::bitwiseAND")),
      Str("bitwiseXOR") -> Func(algos("Number::bitwiseXOR")),
      Str("bitwiseOR") -> Func(algos("Number::bitwiseOR")),
      Str("toString") -> Func(algos("Number::toString")),
    )),
    NamedAddr(PRIMITIVE + ".BigInt") -> IRMap("Record")(List(
      Str("unit") -> BigINum(1),
      Str("unaryMinus") -> Func(algos("BigInt::unaryMinus")),
      Str("bitwiseNOT") -> Func(algos("BigInt::bitwiseNOT")),
      Str("exponentiate") -> Func(algos("BigInt::exponentiate")),
      Str("multiply") -> Func(algos("BigInt::multiply")),
      Str("divide") -> Func(algos("BigInt::divide")),
      Str("remainder") -> Func(algos("BigInt::remainder")),
      Str("add") -> Func(algos("BigInt::add")),
      Str("subtract") -> Func(algos("BigInt::subtract")),
      Str("leftShift") -> Func(algos("BigInt::leftShift")),
      Str("signedRightShift") -> Func(algos("BigInt::signedRightShift")),
      Str("unsignedRightShift") -> Func(algos("BigInt::unsignedRightShift")),
      Str("lessThan") -> Func(algos("BigInt::lessThan")),
      Str("equal") -> Func(algos("BigInt::equal")),
      Str("sameValue") -> Func(algos("BigInt::sameValue")),
      Str("sameValueZero") -> Func(algos("BigInt::sameValueZero")),
      Str("bitwiseAND") -> Func(algos("BigInt::bitwiseAND")),
      Str("bitwiseXOR") -> Func(algos("BigInt::bitwiseXOR")),
      Str("bitwiseOR") -> Func(algos("BigInt::bitwiseOR")),
      Str("toString") -> Func(algos("BigInt::toString")),
    )),
    NamedAddr(SYMBOL_REGISTRY) -> IRList(),
  )
}
