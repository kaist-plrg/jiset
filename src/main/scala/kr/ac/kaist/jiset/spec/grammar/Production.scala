package kr.ac.kaist.jiset.spec.grammar

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.spec.{ Parser, SpecComponent }
import kr.ac.kaist.jiset.util.Useful._

// ECMAScript grammar productions
case class Production(
  lhs: Lhs,
  rhsList: List[Rhs]
) extends SpecComponent {
  // get name
  def name: String = lhs.name

  // get nonterminal names in rhs of lhs
  def getRhsNT: Set[String] = rhsList.flatMap(_.toNTs).map(_.name).toSet

  // get index map
  def getIdxMap: Map[String, (Int, Int)] = (for {
    (rhs, i) <- rhsList.zipWithIndex
    (name, j) <- rhs.allNames.zipWithIndex
  } yield lhs.name + ":" + name -> (i, j)).toMap
}
object Production extends Parser[Production]
