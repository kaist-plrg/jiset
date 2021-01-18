package kr.ac.kaist.jiset.spec.algorithm

import kr.ac.kaist.jiset.spec.grammar._

// syntax-directed algorithm heads
case class SyntaxDirectedHead(
    lhsName: String,
    rhs: Rhs,
    idx: Int,
    subIdx: Int,
    methodName: String,
    withParams: List[String]
) extends Head {
  // name with index and method name
  val name: String = s"$lhsName[$idx,$subIdx].$methodName"

  // prepend `this` parameter and number duplicated params
  val params: List[String] =
    Head.THIS_PARAM :: rename(rhs.getNTs.map(_.name)) ++ withParams

  // rename for duplicated parameters for syntex-directed algorithms
  def rename(params: List[String]): List[String] = {
    val duplicated = params.filter(p => params.count(_ == p) > 1).toSet.toList
    var counter: Map[String, Int] = Map()
    params.map(p => {
      if (duplicated contains p) {
        val n = counter.getOrElse(p, 0)
        counter = counter + (p -> (n + 1))
        p + n.toString
      } else p
    })
  }
}
