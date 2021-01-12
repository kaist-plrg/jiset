package kr.ac.kaist.jiset.spec

import kr.ac.kaist.jiset.algorithm._

import kr.ac.kaist.jiset.util.Useful._
import org.jsoup.nodes._
import scala.collection.mutable.Stack
import scala.util.parsing.combinator._

// ECMASCript abstract algorithms
case class Algo(
    params: List[String],
    tokens: List[Token]
)

object Algo extends RegexParsers {
  // get algorithms
  val TAB = 2
  def apply(elem: Element, code: List[String]): Option[Algo] = optional(Algo(
    params = getParams(elem),
    tokens = getTokens(code)
  ))

  // tokenizer
  object Tokenizer {
    // basic parsers
    lazy val name = "[_-a-zA-Z]+".r
    lazy val word = "[a-zA-Z]+".r
    lazy val number = "[0-9]+".r
    lazy val any = "\\S".r

    // token parsers
    def wrap(str: String) = s"$str$any+$str".r ^^ {
      case s => s.substring(1, s.length - 1)
    }
    lazy val const = wrap("~") ^^ { Const(_) }
    lazy val code = wrap("`") ^^ { Code(_) }
    lazy val value = wrap("\\*") ^^ { Value(_) }
    lazy val id = wrap("_") ^^ { Id(_) }
    lazy val nt = wrap("\\|") ^^ { Nt(_) }
    lazy val sup = "<sup>" ~> "[^<]*".r <~ "</sup>" ^^ {
      case s => Sup(Step(parseAll(rep(token), s).getOrElse(Nil)))
    }
    lazy val url = "<a[^>]*>".r ~> "[^<]*".r <~ "</a>" ^^ { Url(_) }
    lazy val text = (word | number | any) ^^ { Text(_) }
    lazy val token: Parser[Token] =
      const | code | value | id | nt | sup | url | text // TODO grammar

    // step parsers
    lazy val indent = number ~ "." | "*" | "<" ~ rep(any)
    lazy val step: Parser[List[Token]] = indent ~> rep(token)

    // parsing
    def apply(str: String): List[Token] = parse(step, str).get
  }

  // get parameters
  val paramPattern = "[^\\s,()\\[\\]]+".r
  def getParams(elem: Element): List[String] = {
    val str = elem.siblingElements.get(0).text
    val from = str.indexOf("(")
    if (from == -1) Nil
    else paramPattern.findAllMatchIn(str.substring(from)).toList.map(m => {
      val s = m.toString
      s.substring(1, s.length - 1)
    })
  }

  // get tokens
  def getTokens(code: List[String]): List[Token] = {
    val initial = getIndent(code(0))
    var prev = -1
    var tokens = Vector[Token]()
    val nexts = Stack[Next]()
    var k = 0
    def next: Next = { val res = Next(k); k += 1; res }
    code.foreach(step => {
      var indent = getIndent(step)
      if (prev != -1) {
        if (indent > prev) {
          tokens :+= In
          nexts.push(next)
        } else if (indent < prev) {
          tokens :+= next
          while (prev > indent) { prev -= TAB; tokens ++= List(Out, nexts.pop) }
        } else tokens :+= next
      }
      prev = indent
      tokens ++= Tokenizer(step)
    })
    tokens :+= next
    while (prev > initial) { prev -= TAB; tokens ++= List(Out, nexts.pop) }
    tokens.toList
  }
}
