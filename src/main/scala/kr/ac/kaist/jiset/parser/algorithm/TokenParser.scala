package kr.ac.kaist.jiset.parser.algorithm

import kr.ac.kaist.jiset.parser.grammar.ProductionParsers
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.spec.grammar.{ Grammar, NonTerminal, Lhs }
import kr.ac.kaist.jiset.util.Useful._
import scala.collection.mutable.Stack
import kr.ac.kaist.jiset.parser.ECMAScriptParser
import org.jsoup.nodes._
import kr.ac.kaist.jiset.LINE_SEP
import scala.util.parsing.input.CharSequenceReader

// token parsers
object TokenParser extends TokenParsers
trait TokenParsers extends ProductionParsers {
  // exclude LINE_SEP in whitespace
  override val whiteSpace = """[\t ]+""".r
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

  // next counter
  class Counter {
    private var k = 0
    private val nexts = Stack[Next]()
    def next: Next = { val res = Next(k); k += 1; res }
    def push: Unit = { nexts.push(next) }
    def pop: Next = { nexts.pop }
  }

  // step parsers
  private def step(
    grammar: Grammar,
    document: Document,
    counter: Counter
  ): Parser[Int ~ List[Token]] = {
    // helpers
    def tag(name: String): (Parser[String], Parser[String], Parser[String]) =
      (raw"""<$name>\s*""".r, raw"""\s*</$name>""".r, raw"""\s*</$name>\s*""".r)
    def until(target: String) =
      raw"""(?s).*?(?=$target)""".r <~ target ^^ { _.trim }
    def wrap(x: String) = s"[$x][^ $x]+[$x]".r ^^ {
      case s => s.substring(x.length, s.length - x.length)
    }
    // basic parsers
    lazy val name = "[_-a-zA-Z]+".r
    lazy val word = "[a-zA-Z0-9]+".r
    lazy val number = "[0-9]+".r
    lazy val char = "\\S".r
    lazy val whitespace = "\\s*".r

    // tag parsers
    lazy val (table, tableEnd, tableEndWs) = tag("table")
    lazy val (tbody, tbodyEnd, tbodyEndWs) = tag("tbody")
    lazy val (tr, trEnd, trEndWs) = tag("tr")
    lazy val (th, thEnd, thEndWs) = tag("th")
    lazy val (td, tdEnd, tdEndWs) = tag("td")
    lazy val (li, liEnd, liEndWs) = tag("li")
    lazy val (emuNote, emuNoteEnd, emuNoteEndWs) = tag("emu-note")
    lazy val untilLiEnd = until("</li>")
    lazy val untilTdEnd = until("</td>")
    lazy val untilEmuNote = until("</emu-note>")

    // token parsers
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
    lazy val stringValue = "*\"" ~> "[^\"]+".r <~ "\"*" ^^ {
      case sv => s""""${sv.replaceAll("\\\\", "")}""""
    }
    lazy val value = (stringValue | wrap("*")) <~ opt(sub) ^^ { Value(_) } // TODO process the sub
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

    lazy val notRef = "<emu-not-ref>" ~> "[^<]*".r <~ "</emu-not-ref>"
    lazy val text = (notRef | word | number | char) ^^ { Text(_) }

    lazy val token: Parser[Token] =
      gram | const | code | value | id | nt | sup | link | sub | text

    // tabular
    lazy val tabularHead = tr ~> {
      (th ~> "Argument Type" <~ thEndWs) ~
        (th ~> "Result" <~ thEndWs)
    } <~ trEndWs
    lazy val tabularStep: Parser[List[Token]] = tr ~> {
      (td ~> word <~ tdEndWs) ~
        (td ~> untilTdEnd) ^^ {
          case tyName ~ body => {
            // TODO generalize handling _arugment_
            val cond = s"""If Type(_argument_) is $tyName,"""
            var res: List[Token] = parseAll(rep(token), cond).getOrElse(Nil)
            res :+= In; counter.push
            res ++= parse(rep(token), body).getOrElse(Nil)
            res ++= List(counter.next, Out, counter.pop)
            res
          }
        }
    } <~ trEndWs
    lazy val tabular: Parser[List[Token]] = {
      (table ~ tbody) ~> {
        tabularHead ~> rep1(tabularStep) ^^ { _.flatten }
      } <~ (tbodyEnd ~ tableEnd)
    }

    // list
    lazy val list: Parser[List[Token]] = li ~> untilLiEnd <~ opt(newline) ^^ {
      parseAll(rep(token), _).getOrElse(Nil)
    }

    // tokens
    lazy val tokens: Parser[List[Token]] =
      rep(token <~ not(LINE_SEP)) ~ opt(token <~ LINE_SEP) ^^ {
        case ts ~ None => ts
        case ts ~ Some(t) => ts :+ t
      }

    // ignore
    lazy val ignore = "[id=\"" ~ repsep(word, "-") ~ "\"]"

    // indentation parsers
    lazy val space: Parser[Char] = accept(' ');
    lazy val newline = rep(accept('\n'))
    lazy val indent: Parser[Int] = rep(space) ^^ { _.length }

    (indent <~ opt(number ~ "." ~ opt(ignore) | "*")) ~ (tabular | list | tokens)
  }

  // get tokens
  val TAB = 2
  def getTokens(code: Iterable[String])(
    implicit
    grammar: Grammar,
    document: Document
  ): List[Token] = getTokens(code.mkString(LINE_SEP))
  def getTokens(code: String)(
    implicit
    grammar: Grammar,
    document: Document
  ): List[Token] = {
    var initial = -1
    var prev = -1
    var tokens = Vector[Token]()
    val counter = new Counter
    val stepParser = step(grammar, document, counter)

    @annotation.tailrec
    def aux(in: Input): Unit = {
      val pr = stepParser(in)
      if (pr.successful) {
        val indent ~ ts = pr.get
        // handle indent
        if (initial == -1) initial = indent
        else if (prev != -1) {
          if (indent > prev) { tokens :+= In; counter.push }
          else if (indent < prev) {
            tokens :+= counter.next
            while (prev > indent) { prev -= TAB; tokens ++= List(Out, counter.pop) }
          } else tokens :+= counter.next
        }
        prev = indent
        // keep parsing
        tokens ++= ts
        if (!pr.next.atEnd) aux(pr.next)
      } else ??? // parsing fail
    }

    aux(new CharSequenceReader(code))
    tokens :+= counter.next
    while (prev > initial) { prev -= TAB; tokens ++= List(Out, counter.pop) }
    tokens.toList
  }
}
