package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.cfg._

// evaluation cursor
sealed trait Cursor extends IRComponent {
  def currentInst: Option[Inst] = this match {
    case InstCursor(insts) => insts.headOption
    case _ => None
  }
  def ++:(insts: List[Inst]): Cursor = this match {
    case (icursor: InstCursor) => InstCursor(insts ++ icursor.insts)
    case (ncursor: NodeCursor) => ??? // TODO
  }
  def ::(inst: Inst): Cursor = this match {
    case (icursor: InstCursor) => InstCursor(inst :: icursor.insts)
    case (ncursor: NodeCursor) => ??? // TODO
  }
  def replaceWith(inst: Inst): Cursor = this match {
    case (icursor: InstCursor) => InstCursor(List(inst))
    case (ncursor: NodeCursor) => ??? // TODO
  }
}
case class InstCursor(insts: List[Inst]) extends Cursor
case class NodeCursor(nodeOpt: Option[Node]) extends Cursor
