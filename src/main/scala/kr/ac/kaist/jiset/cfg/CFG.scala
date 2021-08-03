package kr.ac.kaist.jiset.cfg

import kr.ac.kaist.jiset.ir.{ Id, UnitWalker }
import kr.ac.kaist.jiset.spec.ECMAScript
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.util.UIdGen

// control flow graph
class CFG(val spec: ECMAScript) extends CFGComponent {
  val trans: Translator = new Translator
  val funcs: List[Function] = spec.algos.flatMap(algo => trans(AlgoOrigin(algo)))
  val bodyFuncMap: Map[Int, Function] = trans.getBodyFuncMap
  val nodes: List[Node] = funcs.flatMap(_.nodes)
  val funcOf: Map[Node, Function] = funcs.flatMap(f => f.nodes.map(_ -> f)).toMap
  val nextOf: Map[Linear, Node] = funcs.flatMap(_.nexts).toMap
  val branchOf: Map[Branch, (Node, Node)] = funcs.flatMap(_.branches).toMap
  val fidMap: Map[Int, Function] = (for (f <- funcs) yield f.uid -> f).toMap
  val iidFuncMap: Map[Int, Function] = (for (f <- funcs) yield f.body.uid -> f).toMap
  val jsonProtocol: JsonProtocol = new JsonProtocol(this)
  val funcMap: Map[String, Function] = (for (f <- funcs) yield f.name -> f).toMap

  //////////////////////////////////////////////////////////////////////////////
  // Helper Functions
  //////////////////////////////////////////////////////////////////////////////
  // unique id generators
  def fidGen: UIdGen[Function] = trans.fidGen
  def nidGen: UIdGen[Node] = trans.nidGen
  // get fids of syntax-directed algorithms
  def getSyntaxFids(lhs: String, method: String): Set[Int] =
    getSyntaxAlgo(lhs, method).map(funcMap(_).uid)

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
