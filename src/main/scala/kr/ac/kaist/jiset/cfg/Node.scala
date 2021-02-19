package kr.ac.kaist.jiset.cfg

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.UId

// CFG nodes
sealed abstract class Node extends UId {
  // enclosing function
  var _func: Function = null
  lazy val func: Function = _func

  // conversion to string
  override def toString: String = (this match {
    case (_: Entry) => "Entry"
    case (_: Exit) => "Exit"
    case (_: Block) => "Block"
    case (_: Call) => "Call"
    case (_: Branch) => "Branch"
  }) + s"[$uid]"
}
case class Entry() extends Node
case class Exit() extends Node
case class Block(var insts: Vector[NormalInst]) extends Node
case class Call(var inst: CallInst) extends Node
case class Branch(cond: Expr) extends Node
