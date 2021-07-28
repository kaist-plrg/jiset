package kr.ac.kaist.jiset.cfg

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.util.UIdGen

// translator from algorithms to CFG functions
class Translator {
  // unique id generators
  val fidGen: UIdGen[Function] = new UIdGen
  val nidGen: UIdGen[Node] = new UIdGen

  // translation from origins to CFG functions
  def apply(origin: Origin): List[Function] = {
    val (func, innerFuncs) = translate(origin)
    func :: innerFuncs
  }

  // translation from origins to CFG functions
  def translate(origin: Origin): (Function, List[Function]) = {
    // algorithm
    val algo = origin.algo

    // body instruction
    val body = origin.body

    // inner functions
    var innerFuncs: List[Function] = Nil

    // edges
    var mnodes: Vector[MNode[Node]] = Vector()
    var forward: Map[Int, Map[EdgeCase, Int]] = Map()

    // assign a new mutable nodes
    var count = 0
    def assign(mnode: MNode[Node]): Int = {
      mnodes :+= mnode
      mnodes.size - 1
    }

    // connect previous edges with a given node
    def connect(prev: List[(Int, EdgeCase)], to: Int): Unit = prev.foreach {
      case (from, edgeCase) =>
        val cases = forward.getOrElse(from, Map())
        forward += from -> (cases + (edgeCase -> to))
    }

    // auxilary function
    def aux(
      prev: List[(Int, EdgeCase)],
      inst: Inst
    ): List[(Int, EdgeCase)] = inst match {
      case condInst @ IIf(_, thenInst, elseInst) =>
        val branch = assign(MBranch(condInst))
        connect(prev, branch)
        val thenPrev = aux(List((branch, Then)), thenInst)
        val elsePrev = aux(List((branch, Else)), elseInst)
        thenPrev ++ elsePrev
      case condInst @ IWhile(_, body) =>
        val branch = assign(MBranch(condInst))
        connect(prev, branch)
        val thenPrev = aux(List((branch, Then)), body)
        connect(thenPrev, branch)
        List((branch, Else))
      case (callInst: CallInst) =>
        val call = assign(MCall(callInst))
        connect(prev, call)
        List((call, Normal))
      case (normalInst: NormalInst) => prev.map(x => (x._1, mnodes(x._1))) match {
        case List((mid, block: MBlock)) =>
          block.insts :+= normalInst
          List((mid, Normal))
        case _ =>
          val block = assign(MBlock(Vector(normalInst)))
          connect(prev, block)
          List((block, Normal))
      }
      case (arrowInst: ArrowInst) =>
        val (f, ifs) = translate(ArrowOrigin(algo, arrowInst))
        innerFuncs ++= f :: ifs
        val arrow = assign(MArrow(arrowInst, f.uid))
        connect(prev, arrow)
        List((arrow, Normal))
      case ISeq(insts) => {
        insts.foldLeft(prev)(aux)
      }
    }

    // aux the body instruction
    val entry = MEntry()
    val exit = MExit()
    val prev = aux(List((assign(entry), Normal)), body)
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

    // check whether the created function is complete or not
    val complete: Boolean = body.isComplete

    // functions
    val func = Function(
      uidGen = fidGen,
      origin = origin,
      entry = entry.node,
      exit = exit.node,
      nodes = mnodes.map(_.node).toSet,
      edges = edges.toSet,
      complete = complete,
    )

    (func, innerFuncs)
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
  private case class MArrow(inst: ArrowInst, fid: Int) extends MLinear[Arrow] {
    def getNode = Arrow(nidGen, inst, fid)
  }
  private case class MBranch(inst: CondInst) extends MNode[Branch] {
    def getNode = Branch(nidGen, inst)
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
