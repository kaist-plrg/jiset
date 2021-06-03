package kr.ac.kaist.jiset.parser.grammar

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.spec.grammar._
import kr.ac.kaist.jiset.util.Useful._
import org.jsoup._
import org.jsoup.nodes._

object GrammarParser {
  def apply(
    implicit
    lines: Array[String],
    document: Document
  ): Grammar = {
    // get lexical grammar list
    val lexElems = List(
      document.getElementById("sec-lexical-grammar"),
      document.getElementById("sec-number-conversions"),
      document.getElementById("sec-universal-resource-identifier-character-classes"),
      document.getElementById("sec-regular-expressions"),
    )
    val lexProdElems = lexElems.map(_
      .getElementsByTag("emu-prodref")
      .toArray(Array[Element]())).flatten
    val lexNames = lexProdElems.toList.map(_.attributes().get("name"))

    // codes for `emu-grammar` tagges elements
    val headPattern = "h\\d".r
    val prodElems = getElems(document, "emu-grammar").filter(elem => {
      val (found, pass) = toArray(elem.previousElementSiblings).foldLeft((false, true)) {
        case ((false, true), prev) => prev.tagName match {
          case headPattern() => (true, prev.text.endsWith("Syntax"))
          case _ => (false, true)
        }
        case (res, _) => res
      }
      found && pass
    })
    val prods = (for {
      prodElem <- prodElems
      body = getRawBody(prodElem).toList
      prodStr <- splitBy(body, "")
      prod <- optional(ProductionParser(prodStr))
    } yield prod).toList

    // partition prods
    val (lexProds, nonLexProds) =
      prods.partition(p => lexNames.contains(p.lhs.name))

    Grammar(lexProds, nonLexProds)
  }

  def parse(src: String): Grammar = {
    def getProds(lines: List[String]) = (for {
      prodStr <- splitBy(lines, "")
      prod <- optional(ProductionParser(prodStr))
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
