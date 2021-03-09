package kr.ac.kaist.jiset.cfg

import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.ir._
import scala.collection.mutable.Queue

// translator from algorithms to CFG functions
object Translator {
  def apply(algo: Algo): Function = {
    // edges
    var mnodes = Vector[MNode]()
    var forward = Map[Int, Map[EdgeCase, Int]]()

    // assign a new mutable nodes
    var count = 0
    def assign(mnode: MNode): Int = {
      mnodes :+= mnode
      mnodes.size - 1
    }

    // connect previous edges with a given node
    def connect(prev: List[(Int, EdgeCase)], to: Int): Unit = prev.foreach {
      case (from, edgeCase) =>
        val cases = forward.getOrElse(from, Map())
        forward += from -> (cases + (edgeCase -> to))
    }

    // translation
    def translate(
      prev: List[(Int, EdgeCase)],
      inst: Inst
    ): List[(Int, EdgeCase)] = inst match {
      case IIf(cond, thenInst, elseInst) =>
        val branch = assign(MBranch(cond))
        connect(prev, branch)
        val thenPrev = translate(List((branch, Then)), thenInst)
        val elsePrev = translate(List((branch, Else)), elseInst)
        thenPrev ++ elsePrev
      case IWhile(cond, body) =>
        val branch = assign(MBranch(cond))
        connect(prev, branch)
        val thenPrev = translate(List((branch, Then)), body)
        connect(thenPrev, branch)
        List((branch, Else))
      case (inst: CallInst) =>
        val call = assign(MCall(inst))
        connect(prev, call)
        List((call, Normal))
      case (inst: NormalInst) => prev.map(x => (x._1, mnodes(x._1))) match {
        case List((id, block: MBlock)) =>
          block.insts += inst
          List((id, Normal))
        case _ =>
          val block = assign(MBlock(Queue(inst)))
          connect(prev, block)
          List((block, Normal))
      }
      case ISeq(insts) => insts.foldLeft(prev)(translate)
    }

    // translate algorithm bodies
    val entry = MEntry()
    val exit = MExit()
    val prev = translate(List((assign(entry), Normal)), algo.getBody)
    connect(prev, assign(exit))

    // functions
    Function(
      algo = algo,
      entry = entry.node,
      exit = exit.node,
      nodes = mnodes.map(_.node).toSet,
      edges = (for {
        (from, cases) <- forward
        edge <- mnodes(from) match {
          case (branch: MBranch) =>
            Some(BranchEdge(branch.node, mnodes(cases(Then)).node, mnodes(cases(Else)).node))
          case (linear: MLinear) =>
            Some(LinearEdge(linear.node, mnodes(cases(Normal)).node))
          case _ => None
        }
      } yield edge).toSet
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
