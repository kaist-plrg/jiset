package kr.ac.kaist.jiset.cfg

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.{ UId, UIdGen }

// CFG nodes
trait Node extends UId[Node] {
  // conversion to string
  override def toString: String = s"${getClass.getSimpleName}[$uid]"
}

// linear nodes
trait Linear extends Node

// entry nodes
case class Entry(uidGen: UIdGen[Node]) extends Linear

// blocks
case class Block(uidGen: UIdGen[Node], insts: List[NormalInst]) extends Linear

// call nodes
case class Call(uidGen: UIdGen[Node], inst: CallInst) extends Linear

// branches
case class Branch(uidGen: UIdGen[Node], cond: Expr) extends Node

// exit nodes
case class Exit(uidGen: UIdGen[Node]) extends Node
