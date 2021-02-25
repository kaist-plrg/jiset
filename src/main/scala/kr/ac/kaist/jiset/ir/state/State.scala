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
  def allocSymbol(desc: String): (Addr, State) = {
    val (newAddr, newHeap) = heap.allocSymbol(desc)
    (newAddr, copy(heap = newHeap))
  }
  def copyObj(addr: Addr): (Addr, State) = {
    val (newAddr, newHeap) = heap.copyObj(addr)
    (newAddr, copy(heap = newHeap))
  }
  def mapObjKeys(addr: Addr): (Addr, State) = {
    val (newAddr, newHeap) = heap.mapObjKeys(addr)
    (newAddr, copy(heap = newHeap))
  }
  def pop(addr: Addr, idx: Value): (Value, State) = {
    val (newAddr, newHeap) = heap.pop(addr, idx)
    (newAddr, copy(heap = newHeap))
  }
}
