package kr.ac.kaist.jiset.spec.algorithm

import kr.ac.kaist.jiset.spec.grammar._
import Param.Kind._

// syntax-directed algorithm heads
case class SyntaxDirectedHead(
    lhsName: String,
    rhs: Rhs,
    idx: Int,
    subIdx: Int,
    methodName: String,
    withParams: List[Param],
    secId: String
) extends Head {
  // name with index and method name
  val name: String = s"$lhsName[$idx,$subIdx].$methodName"

  // prepend `this` parameter and number duplicated params
  val params: List[Param] = Param(THIS_PARAM) :: rename(rhs.getNTs.map(nt => {
    val kind = if (nt.optional) Optional else Normal
    Param(nt.name, kind)
  })) ++ withParams

  // rename for duplicated parameters for syntex-directed algorithms
  def rename(params: List[Param]): List[Param] = {
    val names = params.map(_.name)
    val duplicated =
      names.filter(p => names.count(_ == p) > 1).toSet.toList
    var counter: Map[String, Int] = Map()
    params.map {
      case Param(name, opt) => {
        val newName = if (duplicated contains name) {
          val k = counter.getOrElse(name, 0)
          counter = counter + (name -> (k + 1))
          s"$name$k"
        } else name
        Param(newName, opt)
      }
    }
  }
}
