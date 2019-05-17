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

    def getStrParser(prod: Production): Unit = {
      val Production(lhs, rhsList, semantics) = prod
      val name = lhs.name
      val subName = "sub" + name

      nf.println(s"""  lazy val $name: Parser[String] =""")
      for (
        rhs <- rhsList if (rhs.tokens match {
          case NonTerminal(`name`, _, _) :: _ => false
          case _ => true
        })
      ) nf.println(s"""    seq(${rhs.tokens.map(getTokenParser).mkString(", ")}, $subName) |||""")
      nf.println(s"""    STR_MISMATCH""")
      nf.println(s"""  lazy val ${subName}: Parser[String] =""")
      for (
        rhs <- rhsList if (rhs.tokens match {
          case NonTerminal(`name`, _, _) :: _ => true
          case _ => false
        })
      ) nf.println(s"""    seq(${rhs.tokens.tail.map(getTokenParser).mkString(", ")}, $subName) |||""")
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

    nf.println(s"""package kr.ac.kaist.ase.model""")
    nf.println
    nf.println(s"""import kr.ac.kaist.ase.core._""")
    nf.println(s"""import kr.ac.kaist.ase.parser.ASTParsers""")
    nf.println
    nf.println(s"""object ASTParser extends ASTParsers {""")
    lexProds.foreach(getStrParser)
    // TODO prods.foreach(getParser)
    nf.println(s"""}""")
    nf.close()
  }
}
