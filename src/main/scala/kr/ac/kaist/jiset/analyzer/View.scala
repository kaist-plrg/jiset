package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.cfg._
import domain._

// view abstraction
trait View {
  // view production
  def *[W <: View](that: W): ProdView[this.type, W] = ProdView(this, that)

  // find next views and refined abstract states
  def next(prev: Result[Node], next: Result[Node]): List[Result[View]] = this match {
    case InsensView => List(Result(InsensView, next.st))
    case ProdView(left, right) => for {
      Result(l, ls) <- left.next(prev, next)
      Result(r, rs) <- right.next(prev, next)
    } yield Result(ProdView(l, r), ls ⊓ rs)
    case FlowView(_) => List(Result(FlowView(next.elem), next.st))
  }

  // conversion to string
  override def toString: String = (this match {
    case InsensView => "Insens"
    case ProdView(l, r) => s"$l*$r"
    case FlowView(node) => s"$node"
  })
}

// insensitive view
case object InsensView extends View

// view production
case class ProdView[+V <: View, +W <: View](left: V, right: W) extends View

// flow sensitive view
case class FlowView(node: Node) extends View
