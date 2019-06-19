package kr.ac.kaist.ase.manualModel

import kr.ac.kaist.ase.core._

object ManualModel {
  lazy val initGlobal: Map[Id, Value] = Map(
    Id("ToNumber") -> ToNumber.func,
    Id("ToBoolean") -> ToBoolean.func,
    Id("MulOperation") -> MulOperation.func,
    Id("WrapCompletion") -> WrapCompletion.func
  )
}