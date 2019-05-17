package kr.ac.kaist.ase.generator

import java.io.PrintWriter
import kr.ac.kaist.ase._
import kr.ac.kaist.ase.spec._
import kr.ac.kaist.ase.util.Useful._

object ASTParserGenerator {
  def apply(grammar: Grammar): Unit = {
    val nf = getPrintWriter(s"$MODEL_DIR/ASTParser.scala")
    val Grammar(lexProds, prods) = grammar
    val lexNames = lexProds.map(_.lhs.name).toSet

    def isLL(name: String, rhs: Rhs): Boolean = rhs.tokens match {
      case NonTerminal(`name`, _, _) :: _ => true
      case _ => false
    }

    def getStrParser(prod: Production): Unit = {
      val Production(lhs, rhsList, semantics) = prod
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
      val Production(lhs, rhsList, semantics) = prod
      val Lhs(name, params) = lhs
      val pre = "lazy val"
      val post =
        if (params.length == 0) s"""0[$name] = {"""
        else s"""${params.length}[$name] = memo { case (${params.mkString(", ")}) =>"""

      nf.println(s"""  $pre $name: P$post""")
      for ((rhs, i) <- rhsList.zipWithIndex if !isLL(name, rhs))
        getParsers(name, params, rhs.tokens, rhs.cond, i, false)
      nf.println(s"""    MISMATCH""")
      nf.println(s"""  }""")
      nf.println(s"""  $pre sub$name: R$post""")
      for ((rhs, i) <- rhsList.zipWithIndex if isLL(name, rhs))
        getParsers(name, params, rhs.tokens.tail, rhs.cond, i, true)
      nf.println(s"""    MATCH ^^^ { x => x }""")
      nf.println(s"""  }""")
    }

    def getParsers(name: String, params: List[String], tokens: List[Token], cond: String, idx: Int, isSub: Boolean): Unit = {
      val subName = "sub" + name
      var parser = ("MATCH" /: tokens)(appendParser(_, _))
      val argStrList =
        if (params.length == 0) ""
        else "(" + params.mkString(", ") + ")"
      parser += s""" ~ $subName$argStrList ^^ { case """
      val count = tokens.count(_ match {
        case (_: NonTerminal) | (_: ButNot) => true
        case _ => false
      })
      val ids = (0 until count).map("x" + _.toString)
      val astName = s"$name$idx"

      parser += (if (isSub) {
        if (ids.length == 0) s"""_ ~ y => ((x: $name) => y($astName(x))) }"""
        else s"""_ ~ ${ids.mkString(" ~ ")} ~ y => ((x: $name) => y($astName(x, ${ids.mkString(", ")}))) }"""
      } else {
        if (ids.length == 0) s"""_ ~ y => y(${astName}) }"""
        else s"""_ ~ ${ids.mkString(" ~ ")} ~ y => y(${astName}(${ids.mkString(", ")})) }"""
      })

      if (cond != "") parser = s"""(if(${cond}) ${parser} else MISMATCH)""";
      nf.println(s"""    ${parser} |""")
    }

    def appendParser(base: String, token: Token): String = token match {
      case Terminal(term) => s"""($base <~ term(${getTokenParser(token)}))"""
      case NonTerminal(name, args, optional) =>
        var parser = name
        if (lexNames contains parser) parser = s"""term("$parser", $parser)"""
        else if (args.length != 0) parser += s"""(${args.mkString(", ")})"""
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
    nf.println(s"""import kr.ac.kaist.ase.parser.ASTParsers""")
    nf.println
    nf.println(s"""object ASTParser extends ASTParsers {""")
    lexProds.foreach(getStrParser)
    prods.foreach(getParser)
    nf.println(s"""}""")
    nf.close()
  }
}
