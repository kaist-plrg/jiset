package kr.ac.kaist.jiset.parser.algorithm

import kr.ac.kaist.ires.ir
import kr.ac.kaist.ires.ir.Parser.parseInst
import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.spec.{ ECMAScript, Region }
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.spec.grammar.Grammar
import kr.ac.kaist.jiset.util.Useful._
import org.jsoup.nodes._

object AlgoParser {
  // get algorithms
  def apply(
    elem: Element,
    detail: Boolean = false
  )(
    implicit
    lines: Array[String],
    grammar: Grammar,
    region: Region
  ): List[Algo] = {
    if (detail) println(s"--------------------------------------------------")
    val result = try {
      val heads = HeadParser(elem)
      if (detail) heads.foreach(println(_))
      val range = getRange(elem)
      if (detail) range match {
        case Some((s, e)) => println(s"Range: (${s + 1}, $e)")
        case None => println(s"Cannot detect range.")
      }
      val code =
        if (elem.tagName == "ul") toArray(elem.children).map(li => "* " + li.text)
        else if (elem.tagName == "emu-table") {
          val rows = toArray(elem.select("tr")).filter(row => row.child(0).text != "Argument Type")
          rows.flatMap(row => {
            val typeText = row.child(0).text
            val doTexts = (getElems(row, "emu-alg").headOption match {
              case Some(emuAlg) => {
                val algs = getRawBody(emuAlg)
                val tabCount = getIndent(algs.head)
                algs.map(line => line.substring(tabCount))
              }
              case None => Array("* " + row.child(1).text)
            }).map("  " + _)
            List(s"* If Type(_argument_) is ${typeText},") ++ doTexts //todo! _argument_ should be handled generally
          })
        } else if (elem.tagName == "emu-eqn") {
          // trim until finding first '=' in each line
          getRawBody(elem).map("1. " + _.span(_ != '=')._2.tail.trim)
        } else getRawBody(elem)
      if (detail) {
        code.foreach(println _)
        println(s"====>")
      }
      var printBody = detail && true
      heads.map(h => {
        val body = getBody(h, code)
        if (printBody) {
          println(ir.beautify(body))
          printBody = false
        }
        Algo(h, body)
      })
    } catch {
      case e: Throwable =>
        if (detail) {
          println(s"[Algo] ${e.getMessage}")
          e.getStackTrace.foreach(println _)
        }
        Nil
    }
    if (detail) println(s"--------------------------------------------------")
    result
  }

  // get body instructions
  def getBody(
    head: Head,
    code: Iterable[String]
  )(implicit grammar: Grammar): ir.Inst = {
    import ir._

    val patchedCode = head match {
      case head: MethodHead if head.isLetThisStep(code.head.trim) =>
        code.tail
      case _ => code
    }

    val tokens = TokenParser.getTokens(patchedCode)
    val prefix = head match {
      case (builtin: BuiltinHead) =>
        builtin.origParams.zipWithIndex.map {
          case (x, i) => parseInst(s"app ${x.name} = (GetArgument $ARGS_LIST ${i}i)")
        }
      case _ => Nil
    }
    val body = Compiler(tokens)
    prefix match {
      case Nil => body
      case _ => body match {
        case ISeq(list) => ISeq(prefix ++ list)
        case _ => ISeq(prefix :+ body)
      }
    }
  }
}
