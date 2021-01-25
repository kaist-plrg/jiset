package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.cfg._
import domain._

// view abstraction
trait View {
  // view production
  def *[W <: View](that: W): ProdView[this.type, W] = ProdView(this, that)

  // find next views and refined abstract states
  def next(prev: Result[Node], next: Result[Node]): List[Result[View]] = ???
}

// insensitive view
case object InsensView extends View

// flow sensitive view
case class FlowView(node: Node) extends View

// view production
case class ProdView[+V <: View, +W <: View](left: V, right: W) extends View
