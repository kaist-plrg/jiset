package kr.ac.kaist.ase.model

import kr.ac.kaist.ase.core._

object BaseGlobal {
  private val map: Map[String, Value] = Map(
    "RequireObjectCoercible" -> RequireObjectCoercible.func,
    "ToNumber" -> ToNumber.func,
    "ToBoolean" -> ToBoolean.func,
    "ToObject" -> ToObject.func,
    "ToString" -> ToString.func,
    "MulOperation" -> MulOperation.func,
    "WrapCompletion" -> WrapCompletion.func,
    "ThrowCompletion" -> ThrowCompletion.func,
    "IsDuplicate" -> IsDuplicate.func,
    "HostEnsureCanCompileStrings" -> HostEnsureCanCompileStrings.func,
    "GetTypeOf" -> GetTypeOf.func,
    "IsArrayIndex" -> IsArrayIndex.func,
    "GLOBAL_executionStack" -> NamedAddr("executionStack"),
    "GLOBAL_context" -> Null,
    "GLOBAL" -> NamedAddr("GLOBAL")
  )

  lazy val get: Map[Id, Value] = map.map {
    case (s, v) => Id(s) -> v
  }
}
