package kr.ac.kaist.jiset.parser

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.spec.grammar._
import kr.ac.kaist.jiset.util.Useful._

object GrammarParser {
  def apply(src: String): Grammar = {
    def getProds(lines: List[String]) = (for {
      prodStr <- splitBy(lines, "")
      prod <- optional(Production(prodStr))
    } yield prod).toList

    val lines = src.split(LINE_SEP).toList
    val synIdx = lines.indexOf(Grammar.syntacticHeader)
    val (lexLines, synLines) = lines.splitAt(synIdx) match {
      case (l0, l1) => (l0.tail, l1.tail)
    }
    val lexProds = getProds(lexLines)
    val synProds = getProds(synLines)
    Grammar(lexProds, synProds)
  }
}
