package kr.ac.kaist.jiset.cfg

import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.ir._

// translator from algorithms to CFG functions
object Translator {
  def apply(algo: Algo): Function = {
    // nodes and edges for Translator
    var (nodes, edges) = (Map[Int, Node](), Map[Int, Set[(Edge, Int)]]())

    // register nodes
    def register[T <: Node](n: T): Int = { nodes += n.uid -> n; n.uid }

    // initialization
    val exitNode = Exit()
    val entryNode = Entry(null)
    val (entry, exit) = (register(entryNode), register(exitNode))

    // connect previous edges with a given node
    def connect(prev: List[(Int, Edge)], to: Int): Unit = prev.foreach {
      case (from, edge) =>
        val set = edges.getOrElse(from, Set()) + ((edge, to))
        edges += from -> set
    }

    // connect previous edges with normal instruction
    def connectInst(prev: List[(Int, Edge)], inst: NormalInst): Int = {
      val block = register(Block(inst :: Nil))
      connect(prev, block)
      block
    }

    // translation
    def translate(
      prev: List[(Int, Edge)],
      inst: Inst
    ): List[(Int, Edge)] = inst match {
      case IIf(cond, thenInst, elseInst) =>
        val branch = register(Branch(cond))
        connect(prev, branch)
        val thenPrev = translate(List((branch, CondEdge(true))), thenInst)
        val elsePrev = translate(List((branch, CondEdge(false))), elseInst)
        thenPrev ++ elsePrev
      case IWhile(cond, body) =>
        val branch = register(Branch(cond))
        connect(prev, branch)
        val thenPrev = translate(List((branch, CondEdge(true))), body)
        connect(thenPrev, branch)
        List((branch, CondEdge(false)))
      case (inst: CallInst) =>
        val call = register(Call(inst))
        connect(prev, call)
        List((call, NormalEdge))
      case (inst: NormalInst) =>
        // merge if `prev` is single `Block`
        val block = prev match {
          case (from, edge) :: Nil => nodes.get(from) match {
            case None => ??? // impossible
            case Some(b @ Block(insts, _)) =>
              register(b >> Block(insts :+ inst))
            case _ => connectInst(prev, inst)
          }
          case _ => connectInst(prev, inst)
        }
        List((block, NormalEdge))
      case ISeq(insts) => insts.foldLeft(prev)(translate)
    }

    // translate algo body
    val prev = translate(List((entry, NormalEdge)), algo.getBody)
    connect(prev, exit)

    // patch edges
    for {
      (from, forwards) <- edges
    } nodes(from) match {
      case (n: LinearNode) if forwards.size == 1 =>
        n.next = nodes.get(forwards.head._2)
      case n @ Branch(_, _, _) if forwards.size == 2 =>
        for {
          (edge, to) <- forwards
          toNode = nodes.get(to)
        } edge match {
          case NormalEdge => ??? // impossible
          case CondEdge(true) => n.tnext = toNode
          case CondEdge(false) => n.fnext = toNode
        }
      case _ => ??? // impossible
    }

    // return function
    Function(algo, entryNode, exitNode, nodes.values.toSet)
  }
}
