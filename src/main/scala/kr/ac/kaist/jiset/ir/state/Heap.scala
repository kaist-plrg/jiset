package kr.ac.kaist.jiset.ir.state

// heaps
case class Heap(map: Map[Addr, Obj])
object Heap { def apply(seq: (Addr, Obj)*): Heap = Heap(seq.toMap) }
