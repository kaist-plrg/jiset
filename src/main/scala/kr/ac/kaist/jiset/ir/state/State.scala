package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.cfg.Function
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util.StateMonad

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
  def allocSymbol(desc: Str): (Addr, State) = {
    val (newAddr, newHeap) = heap.allocSymbol(desc.str)
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

  // contains
  def contains(addr: Addr, elem: Value): (Value, State) = heap(addr) match {
    case ListObj(elems) => (Bool(elems contains elem), this)
    case obj @ _ => error("Not a list object: $obj")
  }

  // define new environment id
  def define(id: Id, v: Value): State = copy(env = env.define(id.name, v))

  // getter
  def get(id: String): (Value, State) = (env(id), this)
  def get(addr: Addr, prop: String): (Value, State) = (heap(addr, prop), this)
  def get(str: String, prop: String): (Value, State) = (stringOp(str, prop), this)

  // setter
  def updated(refV: RefValue, v: Value): State = refV match {
    case RefValueId(id) => copy(env = env.define(id, v))
    case RefValueProp(addr, key) => copy(heap = heap.updated(addr, key, v))
    case _ => error(s"illegal reference update: $refV = $v")
  }

  // setter
  def deleted(refV: RefValue): State = refV match {
    case RefValueId(id) => copy(env = env.deleted(id))
    case RefValueProp(addr, key) => copy(heap = heap.deleted(addr, key))
    case _ => error(s"illegal reference delete: $refV")
  }
}
