package kr.ac.kaist.jiset.spec.algorithm

import kr.ac.kaist.jiset.spec.grammar._
import kr.ac.kaist.jiset.spec.grammar.token.NonTerminal
import Param.Kind._

// syntax-directed algorithm heads
case class SyntaxDirectedHead(
  prod: Production,
  idx: Int,
  subIdx: Int,
  rhs: Rhs,
  methodName: String,
  withParams: List[Param]
) extends Head {
  // name of left-hand-side
  val lhsName: String = prod.lhs.name

  // original right-hand-side
  val origRhs: Rhs = prod.rhsList(idx)

  // name with index and method name
  val name: String = s"$lhsName[$idx,$subIdx].$methodName"

  // parameter type names and optional information
  val (types, optional) = {
    val names = rhsNames
    val duplicated = names.filter(p => names.count(_ == p) > 1).toSet
    var counter = Map[String, Int]()
    var optional = Set[String]()
    val types = (THIS_PARAM, lhsName) :: (rhs.getNTs.map {
      case NonTerminal(name, _, opt) =>
        val newName = if (duplicated contains name) {
          val k = counter.getOrElse(name, 0)
          counter += name -> (k + 1)
          s"$name$k"
        } else name
        if (opt) optional += newName
        (newName, name)
    })
    (types, optional)
  }

  // get rhs names
  def rhsNames: List[String] = rhs.getNTs.map(_.name)

  // parameters
  val params: List[Param] = types.map {
    case (name, _) =>
      val kind =
        if (optional contains name) Optional
        else Normal
      Param(name, kind)
  } ++ withParams
}
