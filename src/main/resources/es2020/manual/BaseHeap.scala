package kr.ac.kaist.ase.model

import kr.ac.kaist.ase.core._

object BaseHeap {
  private val map: Map[String, Obj] = Map(
    "executionStack" -> CoreList(Vector()),
    "jobQueue" -> CoreList(Vector())
  )

  lazy val get: Map[Addr, Obj] = map.map {
    case (s, v) => NamedAddr(s) -> v
  }
}
