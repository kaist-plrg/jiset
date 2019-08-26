package kr.ac.kaist.jiset.model

import kr.ac.kaist.jiset.core._

object BaseHeap {
  private val map: Map[String, Obj] = Map(
    "executionStack" -> CoreList(Vector()),
    "jobQueue" -> CoreList(Vector())
  )

  lazy val get: Map[Addr, Obj] = map.map {
    case (s, v) => NamedAddr(s) -> v
  }
}
