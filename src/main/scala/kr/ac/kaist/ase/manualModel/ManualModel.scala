package kr.ac.kaist.ase.manualModel

import kr.ac.kaist.ase.core._

object ManualModel {
  lazy val initGlobal: Map[Id, Value] = Map(
    Id("RequireObjectCoercible") -> RequireObjectCoercible.func,
    Id("ToNumber") -> ToNumber.func,
    Id("ToBoolean") -> ToBoolean.func,
    Id("MulOperation") -> MulOperation.func,
    Id("WrapCompletion") -> WrapCompletion.func,
    Id("executionStack") -> NamedAddr("executionStack"),
    Id("context") -> Null
  )
  lazy val initNamedHeap: Map[Addr, Obj] = Map(
    NamedAddr("executionStack") -> CoreList(Vector())
  )
}