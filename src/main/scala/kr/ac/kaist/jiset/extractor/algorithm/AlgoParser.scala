package kr.ac.kaist.jiset.extractor.algorithm

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.spec.{ ECMAScript, Region }
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.spec.grammar.Grammar
import kr.ac.kaist.jiset.spec.grammar.token.{ NonTerminal, Terminal }
import kr.ac.kaist.jiset.util.JvmUseful._
import kr.ac.kaist.jiset.util.Useful._
import org.jsoup.nodes._

object AlgoParser {
  // get algorithms
  def apply(
    version: String,
    parsedHead: (Element, List[Head]),
    secIds: Map[String, String],
    detail: Boolean
  )(
    implicit
    lines: Array[String],
    grammar: Grammar,
    region: Region,
    document: Document
  ): List[Algo] = {
    val (elem, heads) = parsedHead
    val id: String = getId(elem)
    val result: List[Algo] = try {
      val (start, end) = getRange(elem).get
      // get code
      var code = getRawBody(elem)
      val rawBody = getBody(version, code, secIds, start)
      heads.map(head => Algo(head, id, handleRetCont(rawBody, head), code))
    } catch {
      case e: Throwable => Nil
    }
    result.foreach(algo => (new LocWalker).walk(algo.rawBody))
    result
  }

  // get algorithms from codes
  def getBody(
    version: String,
    code: Array[String],
    secIds: Map[String, String],
    start: Int
  )(
    implicit
    lines: Array[String],
    grammar: Grammar,
    region: Region,
    document: Document
  ): Inst = {
    // get tokens
    val tokens = TokenParser.getTokens(code)

    // get body
    val rawBody = Compiler(version, secIds)(tokens, start)

    // handle special return for continuation
    rawBody
  }

  // handle special return for continuation
  def handleRetCont(inst: Inst, head: Head): Inst = head.name match {
    case x if retContStartNames contains x => RetContWalker.walk(inst)
    case _ => inst
  }
  private val retContStartNames = Set("GeneratorStart", "AsyncGeneratorStart", "AsyncFunctionStart")
  private object RetContWalker extends Walker {
    var inCont = false
    override def walk(inst: Inst): Inst = inst match {
      case (_: ICont) =>
        inCont = true; val res = super.walk(inst); inCont = false; res
      case IReturn(expr) if inCont => {
        val i = IApp(Id("_"), Expr("RET_CONT"), List(expr))
        i.line = inst.line
        i
      }
      case _ => super.walk(inst)
    }
  }

  // get container id
  def getId(elem: Element): String = {
    if (elem.id != "") elem.id
    else if (elem.parent == null) ""
    else getId(elem.parent)
  }

  // get ancestor ids
  def getIds(elem: Element): List[String] = {
    val ids =
      if (elem.parent == null) Nil
      else getIds(elem.parent)
    if (elem.id == "") ids
    else elem.id :: ids
  }
}
