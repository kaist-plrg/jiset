package kr.ac.kaist.jiset.cfg

import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.ir._
import scala.collection.mutable.Queue

// translator from algorithms to CFG functions
object Translator {
  def apply(algo: Algo): Function = {
    // edges
    var forward = Map[MNode, Map[EdgeCase, MNode]]()

    // connect previous edges with a given node
    def connect(prev: List[(MNode, EdgeCase)], to: MNode): Unit = prev.foreach {
      case (from, edgeCase) =>
        val cases = forward.getOrElse(from, Map())
        forward += from -> (cases + (edgeCase -> to))
    }

    // translation
    def translate(
      prev: List[(MNode, EdgeCase)],
      inst: Inst
    ): List[(MNode, EdgeCase)] = inst match {
      case IIf(cond, thenInst, elseInst) =>
        val branch = MBranch(cond)
        connect(prev, branch)
        val thenPrev = translate(List((branch, Then)), thenInst)
        val elsePrev = translate(List((branch, Else)), elseInst)
        thenPrev ++ elsePrev
      case IWhile(cond, body) =>
        val branch = MBranch(cond)
        connect(prev, branch)
        val thenPrev = translate(List((branch, Then)), body)
        connect(thenPrev, branch)
        List((branch, Else))
      case (inst: CallInst) =>
        val call = MCall(inst)
        connect(prev, call)
        List((call, Normal))
      case (inst: NormalInst) => prev match {
        // merge if `prev` is single `Block`
        case List((block: MBlock, _)) =>
          block.insts += inst
          List((block, Normal))
        case _ =>
          val block = MBlock(Queue(inst))
          connect(prev, block)
          List((block, Normal))
      }
      case ISeq(insts) => insts.foldLeft(prev)(translate)
    }

    // translate algorithm bodies
    val entry = MEntry()
    val exit = MExit()
    val prev = translate(List((entry, Normal)), algo.getBody)
    connect(prev, exit)

    // functions
    Function(
      algo = algo,
      entry = entry.node,
      exit = exit.node,
      nodes = forward.keySet.map(_.node) + entry.node + exit.node,
      edges = (forward.collect {
        case (branch: MBranch, cases) =>
          BranchEdge(branch.node, cases(Then).node, cases(Else).node)
        case (linear: MLinear, cases) =>
          LinearEdge(linear.node, cases(Normal).node)
      }).toSet
    )
  }

  // internal mutable nodes
  private trait MNode { val node: Node }
  private trait MLinear extends MNode { val node: Linear }
  private case class MEntry() extends MLinear {
    lazy val node = Entry()
  }
  private case class MBlock(insts: Queue[NormalInst]) extends MLinear {
    lazy val node = Block(insts.toList)
  }
  private case class MCall(inst: CallInst) extends MLinear {
    lazy val node = Call(inst)
  }
  private case class MBranch(cond: Expr) extends MNode {
    lazy val node = Branch(cond)
  }
  private case class MExit() extends MNode {
    lazy val node = Exit()
  }

  // internnal edge cases
  private trait EdgeCase
  private case object Normal extends EdgeCase
  private case object Then extends EdgeCase
  private case object Else extends EdgeCase
}
