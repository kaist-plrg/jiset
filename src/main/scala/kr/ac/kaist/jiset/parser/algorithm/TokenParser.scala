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
    private var appendNext = true
    private val nexts = Stack[Next]()
    def next: Next = { val res = Next(k); k += 1; res }
    def push: Unit = { nexts.push(next) }
    def pop: Next = { nexts.pop }
    def setNoAppendNext: Unit = { appendNext = false }
    def getAppendNext: Boolean = appendNext
  }

  // step parsers
  private def step(
    grammar: Grammar,
    document: Document,
    counter: Counter,
    handleIndent: Boolean,
    secIds: Map[String, Name]
  ): Parser[Int ~ List[Token]] = {
    // helpers
    def tag(name: String): (Parser[String], Parser[String], Parser[String]) =
      (raw"""<$name>\s*""".r, raw"""\s*</$name>""".r, raw"""\s*</$name>\s*""".r)
    def until(target: String) =
      raw"""(?s).*?(?=$target)""".r <~ target ^^ { _.trim }
    def wrap(x: String) = s"[$x][^ $x]+[$x]".r ^^ {
      case s => s.substring(x.length, s.length - x.length)
    }
    def parseStep(line: String): List[Token] =
      parseAll(algoStep, line).getOrElse(Nil)
    def parseStepWithNext(line: String): List[Token] =
      parseStep(line) :+ counter.next
    // basic parsers
    lazy val name = "[_-a-zA-Z]+".r
    lazy val word = "[a-zA-Z0-9]+".r
    lazy val number = "[0-9]+".r
    lazy val char = "\\S".r
    lazy val whitespace = "\\s*".r
    lazy val newline = rep(accept('\n'))
    lazy val space: Parser[Char] = accept(' ');

    // tag parsers
    lazy val (table, tableEnd, tableEndWs) = tag("table")
    lazy val (tbody, tbodyEnd, tbodyEndWs) = tag("tbody")
    lazy val (tr, trEnd, trEndWs) = tag("tr")
    lazy val (th, thEnd, thEndWs) = tag("th")
    lazy val (td, tdEnd, tdEndWs) = tag("td")
    lazy val (li, liEnd, liEndWs) = tag("li")
    lazy val (p, pEnd, pEndWs) = tag("p")
    lazy val (emuAlg, emuAlgEnd, emuAlgEndWs) = tag("emu-alg")
    lazy val untilLiEnd = until("</li>")
    lazy val untilTdEnd = until("</td>")
    lazy val untilEmuNote = until("<emu-note>")
    lazy val untilEmuNoteEnd = until("</emu-note>")
    lazy val untilEmuAlg = until("</emu-alg>")
    lazy val untilPEnd = until("</p>")

    // token parsers
    lazy val gram = "<emu-grammar>" ~> lhs ~ rhs <~ "</emu-grammar>" ^^ {
      case Lhs(lhsName, _) ~ rhs => {
        val caseName = s"${lhsName}:${rhs.allNames.head}"
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
      case secId => {
        // change section link to appropriate text
        secIds.get(secId) match {
          case Some(Name(name)) => Text(name)
          case None => Link(s"unhandled: $secId")
        }
      }
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

    // tabular algorithm
    lazy val tabularHead = tr ~> {
      (th ~> "Argument Type" <~ thEndWs) ~ (th ~> "Result" <~ thEndWs)
    } <~ trEndWs
    // NOTE assume body of algCell is flat
    lazy val algCell = td ~> {
      opt(p ~> untilPEnd) ~> whitespace ~> emuAlg ~> untilEmuAlg ^^ {
        case code =>
          code.split(LINE_SEP).map(parseStepWithNext(_)).toList.flatten
      }
    } <~ tdEndWs
    lazy val pCell = td ~> {
      rep(p ~> untilPEnd <~ whitespace) ^^ {
        case codes =>
          codes.map(parseStepWithNext(_)).flatten
      }
    } <~ tdEndWs
    lazy val dropEmuNote = untilEmuNote <~ untilEmuNoteEnd
    lazy val normalCell = td ~> untilTdEnd ^^ {
      case rawCode => {
        val code = parseAll(dropEmuNote, rawCode).getOrElse(rawCode)
        parseStepWithNext(code)
      }
    }
    lazy val tableCell: Parser[List[Token]] = algCell | pCell | normalCell
    lazy val tabularStep: Parser[List[Token]] = tr ~> {
      (td ~> word <~ tdEndWs) ~ tableCell ^^ {
        case tyName ~ cellTokens => {
          // TODO generalize handling _arugment_
          val cond = s"""If Type(_argument_) is $tyName,"""
          var res: List[Token] = parseStep(cond)
          res :+= In; counter.push
          res ++= cellTokens
          res ++= List(Out, counter.pop)
          res
        }
      }
    } <~ trEndWs
    lazy val tabularAlgo: Parser[List[Token]] =
      table ~ tbody ~ tabularHead ~> rep1(tabularStep) <~ tbodyEnd ~ tableEnd ^^ {
        case tsList =>
          val res = tsList.flatten ++ parseStepWithNext("Assert: *false*.")
          counter.setNoAppendNext
          res
      }

    // list step parser
    lazy val listStep: Parser[List[Token]] =
      li ~> untilLiEnd <~ opt(newline) ^^ { parseStep(_) }

    // algorithm step parser
    lazy val ignore = "[id=\"" ~ repsep(word, "-") ~ "\"]"
    lazy val algoStepPrefix = number ~ "." ~ opt(ignore) | "*"
    lazy val algoStep: Parser[List[Token]] =
      (if (handleIndent) opt(algoStepPrefix) else success("")) ~>
        rep1sep(token, not(LINE_SEP)) <~ opt(LINE_SEP)

    // indentation parser
    lazy val indent: Parser[Int] = rep(space) ^^ { _.length }

    // step parser
    indent ~ (tabularAlgo | listStep | algoStep)
  }

  // get tokens
  val TAB = 2
  def getTokens(code: Iterable[String], secIds: Map[String, Name])(
    implicit
    grammar: Grammar,
    document: Document
  ): List[Token] = getTokens(code.mkString(LINE_SEP), secIds)
  def getTokens(
    code: String,
    secIds: Map[String, Name],
    handleIndent: Boolean = true
  )(
    implicit
    grammar: Grammar,
    document: Document
  ): List[Token] = {
    var initial = -1
    var prev = -1
    val counter = new Counter
    val parser = rep1(step(grammar, document, counter, handleIndent, secIds) ^^ {
      case indent ~ ts => if (handleIndent) {
        var tokens = Vector[Token]()
        if (initial == -1) initial = indent
        else if (prev != -1) {
          if (indent > prev) { tokens :+= In; counter.push }
          else if (indent < prev) {
            tokens :+= counter.next
            while (prev > indent) { prev -= TAB; tokens ++= List(Out, counter.pop) }
          } else tokens :+= counter.next
        }
        prev = indent
        tokens ++= ts
        tokens
      } else ts.toVector
    })

    var tokens = parseAll(parser, code).get.reduce(_ ++ _)
    if (handleIndent) {
      if (counter.getAppendNext) tokens :+= counter.next
      while (prev > initial) { prev -= TAB; tokens ++= List(Out, counter.pop) }
    }
    tokens.toList
  }
}
