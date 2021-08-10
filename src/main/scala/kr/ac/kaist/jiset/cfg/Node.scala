package kr.ac.kaist.jiset.cfg

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.{ UId, UIdGen }
import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Appender._

// CFG nodes
trait Node extends CFGElem with UId[Node] {
  def getInst: Option[Inst] = this match {
    case node: InstNode => Some(node.inst)
    case _ => None
  }
}
object Node {
  // get algorithm step lines for given nodes
  def getLineCount(nodes: Set[Node]): Int = getLines(nodes).size
  def getLines(nodes: Set[Node]): Set[Int] = (for {
    node <- nodes
    inst <- node.getInst
    line <- inst.line
  } yield line).toSet
}

// nodes with instructions
trait InstNode extends Node { val inst: Inst }

// linear nodes
trait Linear extends Node

// entry nodes
case class Entry(uidGen: UIdGen[Node]) extends Linear

// normal nodes
case class Normal(uidGen: UIdGen[Node], inst: NormalInst) extends Linear with InstNode

// call nodes
case class Call(uidGen: UIdGen[Node], inst: CallInst) extends Linear with InstNode

// arrow nodes
case class Arrow(uidGen: UIdGen[Node], inst: ArrowInst, fid: Int) extends Linear with InstNode

// branches
trait Branch extends Node with InstNode { val inst: CondInst }
object Branch { def unapply(branch: Branch) = Some(branch.uidGen, branch.inst) }
case class If(uidGen: UIdGen[Node], inst: CondInst) extends Branch
case class Loop(uidGen: UIdGen[Node], inst: CondInst) extends Branch

// loop continues
case class LoopCont(uidGen: UIdGen[Node]) extends Linear

// exit nodes
case class Exit(uidGen: UIdGen[Node]) extends Node
