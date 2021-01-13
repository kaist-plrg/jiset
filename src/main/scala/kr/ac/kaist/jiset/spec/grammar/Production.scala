package kr.ac.kaist.jiset.spec

import kr.ac.kaist.jiset.util.Useful._

// ECMAScript grammar productions
case class Production(
    var lhs: Lhs,
    var rhsList: List[Rhs]
) {
  def idxMap: Map[String, (Int, Int)] = ???
}
object Production extends ProductionParsers {
  def apply(lines: List[String]): Production = parse(lines.map(revertSpecialCodes))
}
