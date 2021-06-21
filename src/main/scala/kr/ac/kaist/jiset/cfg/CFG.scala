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
