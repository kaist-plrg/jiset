package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.js.cfg
import kr.ac.kaist.jiset.cfg._

// evaluation cursors
sealed trait Cursor extends IRComponent {
  // next cursor
  def next: Option[Cursor] = this match {
    case InstCursor(_, rest) => InstCursor.from(rest)
    case NodeCursor(linear: Linear) => Some(NodeCursor(cfg.nextOf(linear)))
    case NodeCursor(_) => None
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
  def apply(body: Inst): Option[NodeCursor] = for {
    func <- cfg.bodyFuncMap.get(body.uid)
  } yield NodeCursor(func.entry)
}
