package kr.ac.kaist.ase.generator

import java.io.PrintWriter
import kr.ac.kaist.ase._
import kr.ac.kaist.ase.spec._
import kr.ac.kaist.ase.util.Useful._

object ESParserGenerator {
  def apply(grammar: Grammar): Unit = {
    val nf = getPrintWriter(s"$MODEL_DIR/ESParser.scala")
    val Grammar(lexProds, prods) = grammar
    val lexNames = lexProds.map(_.lhs.name).toSet

    def isLL(name: String, rhs: Rhs): Boolean = rhs.tokens match {
      case NonTerminal(`name`, _, _) :: _ => true
      case _ => false
    }

    def getStrParser(prod: Production): Unit = {
      val Production(lhs, rhsList) = prod
      val name = lhs.name
      val subName = "sub" + name

      nf.println(s"""  lazy val $name: Parser[String] =""")
      for (rhs <- rhsList if !isLL(name, rhs))
        nf.println(s"""    seq(${rhs.tokens.map(getTokenParser).mkString(", ")}, $subName) |||""")
      nf.println(s"""    STR_MISMATCH""")
      nf.println(s"""  lazy val ${subName}: Parser[String] =""")
      for (rhs <- rhsList if isLL(name, rhs))
        nf.println(s"""    seq(${rhs.tokens.tail.map(getTokenParser).mkString(", ")}, $subName) |||""")
      nf.println(s"""    STR_MATCH""")
    }

    def getTokenParser(token: Token): String = token match {
      case Terminal(term) => s""""${norm(term)}""""
      case NonTerminal(name, args, optional) =>
        if (optional) s"""strOpt($name)"""
        else name
      case ButNot(base, cases) =>
        val parser = getTokenParser(base)
        val notParser = cases.map(token => getTokenParser(token)).mkString(" ||| ")
        s"""($parser \\ $notParser)"""
      case Lookahead(contains, cases) =>
        val parser = cases.map(c => s"""seq(${c.map(token => getTokenParser(token)).mkString(", ")})""").mkString(" ||| ")
        if (contains) s""""" <~ +($parser)"""
        else s""""" <~ -($parser)"""
      case Unicode(code) => code
      case EmptyToken => "STR_MATCH"
      case NoLineTerminatorToken => "strNoLineTerminator"
      case UnicodeAny => "Unicode"
      case UnicodeIdStart => "IDStart"
      case UnicodeIdContinue => "IDContinue"
    }

    def norm(str: String): String = str
      .replaceAll("""\\""", """\\\\""")
      .replaceAll("""\"""", """\\"""")

    def getParser(prod: Production): Unit = {
      val Production(lhs, rhsList) = prod
      val Lhs(name, params) = lhs
      val pre = "lazy val"
      val post = s"""[$name] = memo {
        case args @ List(${params.mkString(", ")}) =>"""

      nf.println(s"""  $pre $name: P$post""")
      for ((rhs, i) <- rhsList.zipWithIndex if !isLL(name, rhs))
        getParsers(name, rhs.tokens, rhs.cond, i, false)
      nf.println(s"""    MISMATCH""")
      nf.println(s"""    case v => throw WrongNumberOfParserParams(v)""")
      nf.println(s"""  }""")
      nf.println(s"""  $pre sub$name: R$post""")
      for ((rhs, i) <- rhsList.zipWithIndex if isLL(name, rhs))
        getParsers(name, rhs.tokens.tail, rhs.cond, i, true)
      nf.println(s"""    MATCH ^^^ { x => x }""")
      nf.println(s"""    case v => throw WrongNumberOfParserParams(v)""")
      nf.println(s"""  }""")
    }

    def getParsers(name: String, tokens: List[Token], cond: String, idx: Int, isSub: Boolean): Unit = {
      val subName = "sub" + name
      var parser = ("MATCH" /: tokens)(appendParser(_, _))
      parser += s""" ~ $subName(args) ^^ { case """
      val count = tokens.count(_ match {
        case (_: NonTerminal) | (_: ButNot) => true
        case _ => false
      })
      val ids = (0 until count).map("x" + _.toString)
      val astName = s"$name$idx"

      parser += s"_${ids.map(" ~ " + _).mkString("")} ~ y => " + (if (isSub) {
        s"""((x: $name) => y($astName(x, ${ids.map(_ + ", ").mkString("")}args))) }"""
      } else {
        s"""y($astName(${ids.map(_ + ", ").mkString("")}args)) }"""
      })

      if (cond != "") parser = s"""(if(${cond}) ${parser} else MISMATCH)""";
      nf.println(s"""    ${parser} |""")
    }

    def appendParser(base: String, token: Token): String = token match {
      case Terminal(term) => s"""($base <~ term(${getTokenParser(token)}))"""
      case NonTerminal(name, args, optional) =>
        var parser = name
        if (lexNames contains parser) parser = s"""term("$parser", $parser)"""
        else parser += s"""(List(${args.mkString(", ")}))"""
        if (optional) parser = s"""opt($parser)"""
        s"""$base ~ $parser"""
      case ButNot(_, cases) =>
        val parser = getTokenParser(token)
        s"""$base ~ term(\"\"\"$parser\"\"\", $parser)"""
      case Lookahead(contains, cases) =>
        val parser = cases.map(c => s"""seq(${c.map(token => getTokenParser(token)).mkString(", ")})""").mkString(" | ")
        val pre = if (contains) "+" else "-"
        s"""($base <~ ${pre}term("", $parser))"""
      case EmptyToken => base + " ~ MATCH"
      case NoLineTerminatorToken => s"""($base <~ NoLineTerminator)"""
      case _ => base
    }

    nf.println(s"""package kr.ac.kaist.ase.model""")
    nf.println
    nf.println(s"""import kr.ac.kaist.ase.core._""")
    nf.println(s"""import kr.ac.kaist.ase.parser.ESParsers""")
    nf.println(s"""import kr.ac.kaist.ase.error.WrongNumberOfParserParams""")
    nf.println
    nf.println(s"""object ESParser extends ESParsers {""")
    lexProds.foreach(getStrParser)
    prods.foreach(getParser)
    nf.println(s"""  val rules: Map[String, P[AST]] = Map(""")
    nf.println(prods.map {
      case Production(Lhs(name, _), _) => s""""$name" -> $name"""
    }.mkString("," + LINE_SEP))
    nf.println(s"""  )""")
    nf.println(s"""}""")
    nf.close()
  }
}
