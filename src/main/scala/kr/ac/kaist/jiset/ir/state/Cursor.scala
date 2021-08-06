package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.js.cfg
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.util.Useful._

// evaluation cursors
sealed trait Cursor extends IRElem {
  // next cursor
  def next: Option[Cursor] = this match {
    case InstCursor(_, rest) => InstCursor.from(rest)
    case NodeCursor(linear: Linear) => Some(NodeCursor(cfg.nextOf(linear)))
    case NodeCursor(_) => None
  }
  // get instruction of current cursor
  def inst: Option[Inst] = this match {
    case InstCursor(cur, _) => Some(cur)
    case NodeCursor(node) => node match {
      case Entry(_) | Exit(_) | LoopCont(_) => None
      case Normal(_, inst) => Some(inst)
      case Call(_, inst) => Some(inst)
      case Arrow(_, inst, _) => Some(inst)
      case Branch(_, inst) => Some(inst)
    }
  }
}

// generator of evaluation cursors
sealed trait CursorGen[T <: Cursor] {
  def apply(inst: Inst): Option[T]
}

// instruction cursors
case class InstCursor(cur: Inst, rest: List[Inst]) extends Cursor
object InstCursor extends CursorGen[InstCursor] {
  def apply(inst: Inst): Option[InstCursor] = Some(InstCursor(inst, Nil))
  def from(insts: List[Inst]): Option[InstCursor] = insts match {
    case cur :: rest => Some(InstCursor(cur, rest))
    case Nil => None
  }
}

// CFG node cursors
case class NodeCursor(node: Node) extends Cursor
object NodeCursor extends CursorGen[NodeCursor] {
  def apply(body: Inst): Option[NodeCursor] = {
    val func = cfg.bodyFuncMap.getOrElse(body.uid, {
      error(s"impossible node cursor: $body")
    })
    Some(NodeCursor(func.entry))
  }
}
