package kr.ac.kaist.jiset.ir

// Garbage Collector
object GC {
  def apply(st: State, rootValues: Set[Value]): Unit = {
    var addrSet: Set[Addr] = Set()
    val walker = new UnitWalker {
      override def walk(addr: Addr): Unit = addrSet += addr
      override def walk(heap: Heap): Unit = {}
    }
    walker.walk(st)
    rootValues.map(walker.walk)

    val heap = st.heap
    val map = heap.map
    def aux(diff: Set[Addr]): Unit = {
      val prev = addrSet
      diff.foreach(addr => map.get(addr) match {
        case Some(obj) => walker.walk(obj)
        case None =>
      })
      val newDiff = addrSet -- prev
      if (!newDiff.isEmpty) aux(newDiff)
    }
    aux(addrSet)

    val prevSize = map.size
    for {
      (addr, obj) <- map
      addr <- addr match {
        case _: NamedAddr => None
        case dyn => Some(dyn)
      }
      if !(addrSet contains addr)
    } map -= addr
  }
}
