object BaseHeap {
  private val map: Map[String, Obj] = Map(
    "executionStack" -> IRList(Vector()),
    "jobQueue" -> IRList(Vector())
  )

  lazy val get: Map[Addr, Obj] = map.map {
    case (s, v) => NamedAddr(s) -> v
  }
}
