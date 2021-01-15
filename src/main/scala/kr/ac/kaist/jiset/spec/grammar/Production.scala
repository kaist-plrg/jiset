package kr.ac.kaist.jiset.spec.grammar

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.util.Useful._

// ECMAScript grammar productions
case class Production(
    lhs: Lhs,
    rhsList: List[Rhs]
) {
  // get index map
  def getIdxMap: Map[String, (Int, Int)] = (for {
    (rhs, i) <- rhsList.zipWithIndex
    (name, j) <- rhs.names.zipWithIndex
  } yield lhs.name + ":" + name -> (i, j)).toMap

  // conversion to string
  override def toString: String =
    (lhs.toString :: rhsList.map("  " + _.toString)).mkString(LINE_SEP)
}
object Production extends ProductionParsers {
  def apply(lines: List[String]): Production =
    parse(lines.map(revertSpecialCodes))
}
