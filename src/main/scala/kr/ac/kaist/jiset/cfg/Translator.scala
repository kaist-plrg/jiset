package kr.ac.kaist.jiset.cfg

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.util.UIdGen

// translator from algorithms to CFG functions
class Translator {
  // unique id generators
  val fidGen: UIdGen[Function] = new UIdGen
  val nidGen: UIdGen[Node] = new UIdGen

  // nodes to sets of instruciton ids
  private var iidMap: Map[Node, Set[Int]] = Map()
  def getIIdMap: Map[Node, Set[Int]] = iidMap

  // translation from algorithms to CFG functions
  def apply(algo: Algo): Function = {
    // initialization for mutable data
    mnodes = Vector()
    forward = Map()
    count = 0
    instMap = Map()

    // translate algorithm bodies
    val entry = MEntry()
    val exit = MExit()
    val prev = translate(List((assign(entry), Normal)), algo.body)
    connect(prev, assign(exit))

    // edges
    val edges = for {
      (from, cases) <- forward
      edge <- mnodes(from) match {
        case (branch: MBranch) =>
          Some(BranchEdge(branch.node, mnodes(cases(Then)).node, mnodes(cases(Else)).node))
        case (linear: MLinear[_]) =>
          Some(LinearEdge(linear.node, mnodes(cases(Normal)).node))
        case _ => None
      }
    } yield edge

    // add mapping from nodes to instruciton ids
    for {
      (mid, iidSet) <- instMap
      mnode = mnodes(mid)
      node = mnode.node
    } iidMap += node -> iidSet

    // functions
    Function(
      uidGen = fidGen,
      algo = algo,
      entry = entry.node,
      exit = exit.node,
      nodes = mnodes.map(_.node).toSet,
      edges = edges.toSet
    )
  }

  // edges
  private var mnodes: Vector[MNode[Node]] = Vector()
  private var forward: Map[Int, Map[EdgeCase, Int]] = Map()

  // instruciton id map
  private var instMap: Map[Int, Set[Int]] = Map()
  private def addInst(mid: Int, inst: Inst): Unit = {
    val set = instMap.getOrElse(mid, Set())
    instMap += mid -> (set + inst.uid)
  }

  // assign a new mutable nodes
  private var count = 0
  private def assign(mnode: MNode[Node]): Int = {
    mnodes :+= mnode
    mnodes.size - 1
  }

  // connect previous edges with a given node
  private def connect(prev: List[(Int, EdgeCase)], to: Int): Unit = prev.foreach {
    case (from, edgeCase) =>
      val cases = forward.getOrElse(from, Map())
      forward += from -> (cases + (edgeCase -> to))
  }

  // translation
  private def translate(
    prev: List[(Int, EdgeCase)],
    inst: Inst
  ): List[(Int, EdgeCase)] = inst match {
    case IIf(cond, thenInst, elseInst) =>
      val branch = assign(MBranch(cond))
      addInst(branch, inst)
      connect(prev, branch)
      val thenPrev = translate(List((branch, Then)), thenInst)
      val elsePrev = translate(List((branch, Else)), elseInst)
      thenPrev ++ elsePrev
    case IWhile(cond, body) =>
      val branch = assign(MBranch(cond))
      addInst(branch, inst)
      connect(prev, branch)
      val thenPrev = translate(List((branch, Then)), body)
      connect(thenPrev, branch)
      List((branch, Else))
    case (inst: CallInst) =>
      val call = assign(MCall(inst))
      addInst(call, inst)
      connect(prev, call)
      List((call, Normal))
    case (inst: NormalInst) => prev.map(x => (x._1, mnodes(x._1))) match {
      case List((mid, block: MBlock)) =>
        addInst(mid, inst)
        block.insts :+= inst
        List((mid, Normal))
      case _ =>
        val block = assign(MBlock(Vector(inst)))
        addInst(block, inst)
        connect(prev, block)
        List((block, Normal))
    }
    case (inst: ArrowInst) => ??? // TODO
    case ISeq(insts) => {
      insts.foldLeft(prev)(translate)
    }
  }

  // internal mutable nodes
  private trait MNode[+T <: Node] {
    lazy val node: T = getNode
    protected def getNode: T
  }
  private trait MLinear[T <: Linear] extends MNode[T]
  private case class MEntry() extends MLinear[Entry] {
    def getNode = Entry(nidGen)
  }
  private case class MBlock(var insts: Vector[NormalInst]) extends MLinear[Block] {
    def getNode = Block(nidGen, insts.toList)
  }
  private case class MCall(inst: CallInst) extends MLinear[Call] {
    def getNode = Call(nidGen, inst)
  }
  private case class MBranch(cond: Expr) extends MNode[Branch] {
    def getNode = Branch(nidGen, cond)
  }
  private case class MExit() extends MNode[Exit] {
    def getNode = Exit(nidGen)
  }

  // internnal edge cases
  private trait EdgeCase
  private case object Normal extends EdgeCase
  private case object Then extends EdgeCase
  private case object Else extends EdgeCase
}
