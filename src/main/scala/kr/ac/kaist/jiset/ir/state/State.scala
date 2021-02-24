package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.cfg.Function

// states
case class State(env: Env, heap: Heap) {
  def allocList(list: List[Value]): (Addr, State) = {
    val (newAddr, newHeap) = heap.allocList(list)
    (newAddr, copy(heap = newHeap))
  }
  def allocMap(ty: Ty, svMap: Map[String, Value]): (Addr, State) = {
    val (newAddr, newHeap) = heap.allocMap(ty, svMap)
    (newAddr, copy(heap = newHeap))
  }
}
