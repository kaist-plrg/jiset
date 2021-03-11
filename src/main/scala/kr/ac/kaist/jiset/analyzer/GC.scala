package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.analyzer.domain.obj.BasicDomain._
import kr.ac.kaist.jiset.ir._
import scala.annotation.tailrec

// garbage collection
private class GC(heap: AbsHeap, root: AbsValue) {
  lazy val result: AbsHeap = {
    visit(root)
    val total = heap.keySet
    val unreachable = total -- visited
    heap -- unreachable
  }

  private var visited = Set[Addr]()

  private def visit(value: AbsValue): Unit = {
    visit(value.pure)
    visit(value.comp)
  }

  private def visit(obj: AbsObj): Unit = obj match {
    case AbsObj.Top => ???
    case AbsObj.MapElem(_, map) =>
      for (v <- map.map.values) visit(v.value)
    case AbsObj.ListElem(list) =>
      visit(list.value)
    case _ => Set()
  }

  private def visit(pure: AbsPure): Unit = visit(pure.addr)

  private def visit(comp: AbsComp): Unit = for ((_, (value, target)) <- comp.map) {
    visit(value)
    visit(target)
  }

  private def visit(addr: AbsAddr): Unit = for (a <- addr) visit(a)

  private def visit(addr: Addr): Unit = if (!(visited contains addr)) {
    visited += addr
    visit(heap(addr))
  }
}
object GC {
  def apply(heap: AbsHeap, root: AbsValue): AbsHeap = {
    (new GC(heap, root)).result
  }
}
