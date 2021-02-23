package kr.ac.kaist.jiset.cfg

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.UId

// CFG nodes
trait Node extends UId {
  // conversion to string
  override def toString: String = s"${getClass.getSimpleName}[$uid]"
}

// linear nodes
trait Linear extends Node

// entry nodes
case class Entry() extends Linear

// blocks
case class Block(insts: List[NormalInst]) extends Linear

// call nodes
case class Call(inst: CallInst) extends Linear

// branches
case class Branch(cond: Expr) extends Node

// exit nodes
case class Exit() extends Node
