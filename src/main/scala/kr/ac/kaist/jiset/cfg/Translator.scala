package kr.ac.kaist.jiset.cfg

import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.ires.ir._

// translator from algorithms to CFG functions
object Translator {
  def apply(algo: Algo): Function = {
    // nodes and edges
    var (nodes, edges) = (Set[Node](), Map[Node, Set[(Edge, Node)]]())

    // register nodes
    def register[T <: Node](node: T): T = { nodes += node; node }

    // connect previous edges with a given node
    def connect(prev: List[(Node, Edge)], to: Node): Unit = prev.foreach {
      case (from, edge) =>
        val set = edges.getOrElse(from, Set()) + ((edge, to))
        edges += from -> set
    }

    // get a block node
    def getBlock(prev: List[(Node, Edge)]): Block = prev match {
      case (block: Block, _) :: Nil => block
      case _ =>
        val block = register(Block(Vector()))
        connect(prev, block)
        block
    }

    // translation
    def translate(
      prev: List[(Node, Edge)],
      inst: Inst
    ): List[(Node, Edge)] = inst match {
      case IIf(cond, thenInst, elseInst) =>
        val branch = register(Branch(cond))
        connect(prev, branch)
        val thenPrev = translate(List((branch, CondEdge(true))), thenInst)
        val elsePrev = translate(List((branch, CondEdge(false))), elseInst)
        thenPrev ++ elsePrev
      case IWhile(cond, body) =>
        val branch = register(Branch(cond))
        val thenPrev = translate(List((branch, CondEdge(true))), body)
        connect(thenPrev, branch)
        List((branch, CondEdge(false)))
      case (inst: CallInst) =>
        val call = register(Call(inst))
        connect(prev, call)
        List((call, NormalEdge))
      case (inst: NormalInst) =>
        val block = getBlock(prev)
        block.insts :+= inst
        List((block, NormalEdge))
      case ISeq(insts) => insts.foldLeft(prev)(translate)
    }

    // initialization
    val (entry, exit) = (register(Entry()), register(Exit()))
    val prev = translate(List((entry, NormalEdge)), algo.body)
    connect(prev, exit)
    Function(entry, exit, nodes, edges)
  }
}
