package kr.ac.kaist.jiset.cfg

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.spec.ECMAScript
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.util.UIdGen

// control flow graph
class CFG(val spec: ECMAScript) {
  val trans: Translator = new Translator
  val funcs: List[Function] = spec.algos.flatMap(algo => trans(AlgoOrigin(algo)))
  val nodes: List[Node] = funcs.flatMap(_.nodes)
  val edges: List[Edge] = funcs.flatMap(_.edges)
  val funcOf: Map[Node, Function] = funcs.flatMap(f => f.nodes.map(_ -> f)).toMap
  val next: Map[Linear, Node] =
    (edges.collect { case LinearEdge(x, y) => x -> y }).toMap
  val thenNext: Map[Branch, Node] =
    (edges.collect { case BranchEdge(x, y, _) => x -> y }).toMap
  val elseNext: Map[Branch, Node] =
    (edges.collect { case BranchEdge(x, _, y) => x -> y }).toMap
  val nexts: Map[Node, Set[Node]] = (edges.map {
    case LinearEdge(x, y) => x -> Set(y)
    case BranchEdge(x, y, z) => x -> Set(y, z)
  }).toMap
  val fidMap: Map[Int, Function] = (for (f <- funcs) yield f.uid -> f).toMap
  val algo2fid: Map[String, Int] = (for (f <- funcs) yield f.name -> f.uid).toMap
  val iid2fid: Map[Int, Int] = (for (f <- funcs) yield f.body.uid -> f.uid).toMap
  val jsonProtocol: JsonProtocol = new JsonProtocol(this)

  //////////////////////////////////////////////////////////////////////////////
  // Helper Functions
  //////////////////////////////////////////////////////////////////////////////
  // unique id generators
  def fidGen: UIdGen[Function] = trans.fidGen
  def nidGen: UIdGen[Node] = trans.nidGen
  // get fids of syntax-directed algorithms
  def getSyntaxFids(lhs: String, method: String): Set[Int] =
    getSyntaxAlgo(lhs, method).map(algo2fid(_))

  // syntax algorithms
  lazy val syntaxAlgos: List[(Algo, SyntaxDirectedHead)] = spec.algos.collect {
    case algo @ Algo(head: SyntaxDirectedHead, _, _, _) => (algo, head)
  }

  // get syntax directed algos
  def getSyntaxAlgo(lhs: String, method: String): Set[String] = {
    val grammar = spec.grammar
    var names = Set[String]()
    var visited = Set[String]()
    def aux(lhs: String): Unit = if (!(visited contains lhs)) {
      visited += lhs
      val rhsList = grammar.nameMap(lhs).rhsList
      var excludes = Set[Int]()

      // direct
      for ((algo, head) <- syntaxAlgos) {
        if (head.lhsName == lhs && head.methodName == method) {
          excludes += head.idx
          names += algo.name
        }
      }

      // chain
      for {
        idx <- 0 until rhsList.length
        if !(excludes contains idx)
        rhs = rhsList(idx)
        nt <- rhs.toNTs match {
          case List(nt) => Some(nt)
          case _ => None
        }
      } aux(nt.name)
    }

    aux(lhs)
    names
  }

  // get constant names
  def getNames = {
    var consts: Set[String] = Set()
    var intrinsics: Set[String] = Set()
    var symbols: Set[String] = Set()
    object ConstExtractor extends UnitWalker {
      override def walk(id: Id) = {
        if (id.name startsWith "CONST_") consts += id.name
        if (id.name startsWith "INTRINSIC_") intrinsics += id.name
        if (id.name startsWith "SYMBOL_") symbols += id.name
      }
    }
    for (algo <- spec.algos) ConstExtractor.walk(algo.rawBody)
    (
      consts,
      intrinsics,
      symbols,
      spec.grammar.nameMap.keySet
    )
  }
}
