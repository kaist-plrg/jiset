package kr.ac.kaist.jiset.cfg

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.{ UId, UIdGen }
import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Appender._

// CFG nodes
trait Node extends CFGElem with UId[Node]

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
trait Branch extends Node { val inst: CondInst }
object Branch { def unapply(branch: Branch) = Some(branch.uidGen, branch.inst) }
case class If(uidGen: UIdGen[Node], inst: CondInst) extends Branch
case class Loop(uidGen: UIdGen[Node], inst: CondInst) extends Branch

// loop continues
case class LoopCont(uidGen: UIdGen[Node]) extends Linear

// exit nodes
case class Exit(uidGen: UIdGen[Node]) extends Node
