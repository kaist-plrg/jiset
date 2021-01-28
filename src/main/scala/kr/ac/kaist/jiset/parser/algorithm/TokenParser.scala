package kr.ac.kaist.jiset.parser.algorithm

import kr.ac.kaist.jiset.parser.grammar.ProductionParsers
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.spec.grammar.{ Grammar, NonTerminal, Lhs }
import kr.ac.kaist.jiset.util.Useful._
import scala.collection.mutable.Stack
import org.jsoup.nodes._

// token parsers
object TokenParser extends TokenParsers
trait TokenParsers extends ProductionParsers {
  // token list from string
  def listFrom(str: String): List[Token] = parseAll(tokenStrList, str).get

  // token list parser
  lazy val tokenStrList: Parser[List[Token]] = rep(tokenStr)
  lazy val tokenStr: Parser[Token] = {
    lazy val const = getTokenParser("const") ^^ { Const(_) }
    lazy val code = getTokenParser("code") ^^ { Code(_) }
    lazy val value = getTokenParser("value") ^^ { Value(_) }
    lazy val id = getTokenParser("id") ^^ { Id(_) }
    lazy val nt = getTokenParser("nt") ^^ { Nt(_) }
    lazy val sup = getTokenParser(
      "sup",
      rep(token) ^^ { case ts => Sup(ts) }
    )
    lazy val link = getTokenParser("link") ^^ { Link(_) }
    lazy val grtext = "[^,\\[\\]]+".r
    lazy val grammar = getTokenParser(
      "grammar",
      grtext ~ ("[" ~> repsep(grtext, ",") <~ "]") ^^ { case g ~ s => Gr(g, s) }
    )
    lazy val sub = getTokenParser(
      "sub",
      rep(token) ^^ { case ts => Sub(ts) }
    )

    lazy val next = getTokenParser("next") ^^ { s => Next(s.toInt) }
    lazy val in = getTokenParser("in") ^^^ In
    lazy val out = getTokenParser("out") ^^^ Out

    lazy val text = "\\S+".r ^^ { Text(_) }

    lazy val token: Parser[Token] = (
      const | code | value | id | nt | sup | link | grammar | sub |
      next | in | out | text
    )

    token
  }

  // get token parsers
  def getTokenParser[T](name: String, parser: Parser[T]) = s"$name:{" ~> parser <~ "}"
  def getTokenParser[T](name: String) =
    s"$name:\\{[^{}]*\\}".r ^^ { case s => s.drop(name.length + 2).dropRight(1) }

  // step parsers
  def step(grammar: Grammar, document: Document): Parser[List[Token]] = {
    lazy val name = "[_-a-zA-Z]+".r
    lazy val word = "[a-zA-Z0-9]+".r
    lazy val number = "[0-9]+".r
    lazy val char = "\\S".r

    // token parsers
    def wrap(x: String) = s"[$x][^ $x]+[$x]".r ^^ {
      case s => s.substring(x.length, s.length - x.length)
    }
    lazy val gram = "<emu-grammar>" ~> lhs ~ rhs <~ "</emu-grammar>" ^^ {
      case Lhs(lhsName, _) ~ rhs => {
        val caseName = s"${lhsName}:${rhs.names.head}"
        grammar.idxMap.get(caseName) match {
          case Some((idx, _)) => {
            val subs = rhs.tokens.flatMap {
              case NonTerminal(n, _, _) => Some(n)
              case _ => None
            }
            Gr(s"${lhsName}${idx}", subs)
          }
          case None => error("`Tokenizer`: no such `caseName` in index map")
        }
      }
    }
    lazy val const = wrap("~") ^^ { Const(_) }
    lazy val code = wrap("`") ^^ { Code(_) }
    lazy val value = wrap("*") <~ opt(sub) ^^ { Value(_) } // TODO process the sub
    lazy val id = wrap("_") ^^ { Id(_) }
    lazy val nt = wrap("|") ^^ {
      case x if x.endsWith("_opt") => Nt(x.dropRight("_opt".length))
      case x => Nt(x)
    }
    lazy val sup = "<sup>" ~> "[^<]*".r <~ "</sup>" ^^ {
      case s => Sup(parseAll(rep(token), s).getOrElse(Nil))
    }
    lazy val link = "<emu-xref href=\"#" ~> "[^\"]*".r <~ "\"" <~ opt("[^>]*".r) <~ "></emu-xref>" ^^ {
      case id => Link("") // TODO convert id to corresponding name
    }
    lazy val sub = "<sub>" ~> "[^<]*".r <~ "</sub>" ^^ {
      case s =>
        // until 2017, [AWAIT] and [YIELD] are inside the sub as a parameter
        // after 2019, ℝ, ℤ, and 𝔽 are inside the sub to distinguish between different numeric kinds
        Sub(parseAll(rep(token), s).getOrElse(Nil))
    }
    lazy val text = (word | number | char) ^^ { Text(_) }
    lazy val token: Parser[Token] =
      gram | const | code | value | id | nt | sup | link | sub | text

    // ignore
    lazy val ignore = "[id=\"" ~ repsep(word, "-") ~ "\"]"

    // indentation parsers
    lazy val indent = number ~ "." ~ opt(ignore) | "*" | "<" ~ rep(char)
    opt(indent) ~> rep(token)
  }

  // get tokens
  val TAB = 2
  def getTokens(line: String)(
    implicit
    grammar: Grammar,
    document: Document
  ): List[Token] = parseAll(step(grammar, document), line).get
  def getTokens(code: Iterable[String])(
    implicit
    grammar: Grammar,
    document: Document
  ): List[Token] = {
    val initial = getIndent(code.head)
    var prev = -1
    var tokens = Vector[Token]()
    val nexts = Stack[Next]()
    var k = 0
    def next: Next = { val res = Next(k); k += 1; res }
    code.foreach(line => {
      var indent = getIndent(line)
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
      tokens ++= getTokens(line)
    })
    tokens :+= next
    while (prev > initial) { prev -= TAB; tokens ++= List(Out, nexts.pop) }
    tokens.toList
  }
}
