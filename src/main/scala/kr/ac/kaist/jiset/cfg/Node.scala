package kr.ac.kaist.jiset.cfg

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.UId

// CFG nodes
sealed abstract class Node extends UId {
  // enclosing function
  var _func: Function = null
  lazy val func: Function = _func

  def nexts: List[Node] = this match {
    case (n: LinearNode) => List(n.next).flatten
    case Branch(_, t, f) => List(t, f).flatten
    case _ => List()
  }

  // conversion to string
  override def toString: String = (this match {
    case (_: Entry) => "Entry"
    case (_: Exit) => "Exit"
    case (_: Block) => "Block"
    case (_: Call) => "Call"
    case (_: Branch) => "Branch"
  }) + s"[$uid]"
}
case class Exit() extends Node
case class Branch(
  cond: Expr,
  var tnext: Option[Node] = None,
  var fnext: Option[Node] = None
) extends Node

sealed abstract class LinearNode extends Node { var next: Option[Node] }
case class Entry(var next: Option[Node] = None) extends LinearNode
case class Block(
  insts: List[NormalInst],
  var next: Option[Node] = None
) extends LinearNode
case class Call(
  inst: CallInst,
  var next: Option[Node] = None
) extends LinearNode
