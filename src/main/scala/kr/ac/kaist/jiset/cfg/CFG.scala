package kr.ac.kaist.jiset.cfg

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.spec.ECMAScript
import kr.ac.kaist.jiset.spec.algorithm.{ NormalHead, MethodHead }
import kr.ac.kaist.jiset.util.UIdGen

// control flow graph
class CFG(val spec: ECMAScript) {
  val fidGen: UIdGen = new UIdGen
  val nidGen: UIdGen = new UIdGen
  val funcs: List[Function] = spec.algos.map(Translator(_, fidGen, nidGen))
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

  //////////////////////////////////////////////////////////////////////////////
  // Helper Functions
  //////////////////////////////////////////////////////////////////////////////
  // initial global variables and heaps
  def getGlobal: (Map[String, Value], Map[Loc, Obj]) = {
    var env = globalMethods
    var heap = Map[Loc, Obj]()

    val (consts, intrinsics, symbols) = getNames

    // constants
    for (x <- consts) {
      val const = Const(x.substring("CONST_".length))
      env += x -> const
    }

    // intrinsics
    for (x <- intrinsics) {
      val name = x.substring("INTRINSIC_".length).replaceAll("_", ".")
      env += x -> NamedAddr(s"%$name%")
    }

    // symbols
    for (x <- symbols) {
      val desc = x.substring("SYMBOL_".length).replaceAll("_", ".")
      val loc = NamedAddr(s"@$desc")
      env += x -> loc
      heap += loc -> SymbolObj(desc)
    }

    (env, heap)
  }

  // get fids of syntax-directed algorithms
  def getSyntaxFids(lhs: String, method: String): Set[Int] =
    spec.getSyntaxAlgo(lhs, method).map(algo2fid(_))

  //////////////////////////////////////////////////////////////////////////////
  // Private Helper Functions
  //////////////////////////////////////////////////////////////////////////////
  // get global methods
  private def globalMethods: Map[String, Value] = (for {
    func <- funcs
    name <- func.algo.head match {
      case NormalHead(name, _) => Some(name)
      case MethodHead(base, methodName, _, _) => Some(s"${base}DOT${methodName}")
      case _ => None
    }
  } yield name -> Clo(func.uid)).toMap

  // get constant names
  private def getNames: (Set[String], Set[String], Set[String]) = {
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
    (consts, intrinsics, symbols)
  }
}
