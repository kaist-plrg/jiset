package kr.ac.kaist.jiset.ir

// TODO Garbage Collector
object GC {
  def gc(st: State): Unit = {}
  // {
  //   var addrSet: Set[Addr] = Set()
  //   val walker = new UnitWalker {
  //     override def walk(addr: Addr): Unit = addrSet += addr
  //     override def walk(heap: Heap): Unit = {}
  //   }
  //   walker.walk(st)

  //   val heap = st.heap
  //   val map = heap.map
  //   def aux(diff: Set[Addr]): Unit = {
  //     val prev = addrSet
  //     diff.foreach(addr => map.get(addr) match {
  //       case Some(obj) => walker.walk(obj)
  //       case None =>
  //     })
  //     val newDiff = addrSet -- prev
  //     if (!newDiff.isEmpty) aux(newDiff)
  //   }
  //   aux(addrSet)

  //   val removeSet = (map.keySet -- addrSet)
  //   st.copy(heap = heap.copy(map = map -- removeSet))
  // }
}
