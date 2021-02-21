package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.cfg._
import domain._

// view abstraction
trait View {
  // find next views and refined abstract states
  def next(cur: Result[Node], next: Result[Node]): View = this match {
    case MultiView(list) => MultiView(list.map(_.next(cur, next)))
    case FlowView(_) => FlowView(next.elem)
    case ParamTypeView(_) => next.elem match {
      case (entry: Entry) => ???
      case _ => this
    }
  }

  // conversion to string
  override def toString: String = (this match {
    case MultiView(list) => list.mkString("x")
    case FlowView(node) => s"$node"
    case ParamTypeView(types) => types.mkString("[", ", ", "]")
  })
}
object View {
  val Nil = MultiView()
  def apply(seq: View*): MultiView = MultiView(seq.toList)
  implicit def node2view(node: Node) = FlowView(node)
  implicit def types2view(tys: List[Type]) = ParamTypeView(tys)
}

// multiple view
case class MultiView(list: List[View]) extends View {
  // view production
  def ::[W <: View](view: View): MultiView = MultiView(view :: list)
}
object MultiView {
  def apply(seq: View*): MultiView = MultiView(seq.toList)
}

// flow sensitive view
case class FlowView(node: Node) extends View

// parameter type sensitive view
case class ParamTypeView(tys: List[Type]) extends View
