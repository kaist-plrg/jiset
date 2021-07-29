package kr.ac.kaist.jiset.cfg

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.{ UId, UIdGen }

// CFG nodes
trait Node extends CFGComponent with UId[Node] {
  // conversion to string
  override def toString: String = s"${getClass.getSimpleName}[$uid]"
}

// linear nodes
trait Linear extends Node

// entry nodes
case class Entry(uidGen: UIdGen[Node]) extends Linear

// normal nodes
case class Normal(uidGen: UIdGen[Node], inst: NormalInst) extends Linear

// call nodes
case class Call(uidGen: UIdGen[Node], inst: CallInst) extends Linear

// arrow nodes
case class Arrow(uidGen: UIdGen[Node], inst: ArrowInst, fid: Int) extends Linear

// branches
case class Branch(uidGen: UIdGen[Node], inst: CondInst) extends Node

// exit nodes
case class Exit(uidGen: UIdGen[Node]) extends Node
