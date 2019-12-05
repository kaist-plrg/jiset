package kr.ac.kaist.jiset.generator

import java.io.PrintWriter
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.util.Useful._

object ParserGenerator {
  def apply(packageName: String, modelDir: String, grammar: Grammar, debug: Boolean = false): Unit = {
    val nf = getPrintWriter(s"$modelDir/Parser.scala")
    val Grammar(lexProds, prods) = grammar
    val lexNames = lexProds.map(_.lhs.name).toSet
    val terminalTokens = prods.foldLeft(Set[String]()) {
      case (set, Production(lhs, rhsList)) => rhsList.foldLeft(set) {
        case (set, Rhs(tokens, _)) => tokens.foldLeft(set) {
          case (set, Terminal(t)) => set + t
          case (set, _) => set
        }
      }
    }

    def isLL(name: String, rhs: Rhs): Boolean = rhs.tokens match {
      case NonTerminal(`name`, _, _) :: _ => true
      case _ => false
    }

    def getStrParser(prod: Production): Unit = {
      val Production(lhs, rhsList) = prod
      val name = lhs.name

      val noLLs = rhsList.filter(!isLL(name, _))
      val LLs = rhsList.filter(isLL(name, _))

      val pre = if (LLs.isEmpty) "" else "resolveLL"
      nf.println(s"""  lazy val $name: Lexer = $pre(""")
      nf.print(noLLs.map {
        case rhs => s"""    s(${rhs.tokens.map(getTokenParser).mkString(", ")})"""
      }.mkString(" |||" + LINE_SEP))
      if (!LLs.isEmpty) nf.print(LLs.map {
        case rhs => s"""    sLL(${rhs.tokens.drop(1).map(getTokenParser).mkString(", ")})"""
      }.mkString("," + LINE_SEP, " |||" + LINE_SEP, ""))
      nf.println
      nf.println(s"""  )""")
    }

    def getTokenParser(token: Token): String = token match {
      case Terminal(term) => s""""${norm(term)}""""
      case NonTerminal(name, args, optional) =>
        if (optional) s"""opt($name)"""
        else name
      case ButNot(base, cases) =>
        val parser = getTokenParser(base)
        val notParser = cases.map(token => getTokenParser(token)).mkString("(", " ||| ", ")")
        s"""($parser \\ $notParser)"""
      case Lookahead(contains, cases) =>
        val parser = cases.map(c => s"""s(${c.map(token => getTokenParser(token)).mkString(", ")})""").mkString(" ||| ")
        if (contains) s"""+$parser"""
        else s"""-$parser"""
      case Unicode(code) => code
      case EmptyToken => "empty"
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
      val Lhs(name, rawParams) = lhs
      val params = rawParams.map("p" + _)

      val noLLs = rhsList.zipWithIndex.filter { case (rhs, _) => !isLL(name, rhs) }
      val LLs = rhsList.zipWithIndex.filter { case (rhs, _) => isLL(name, rhs) }

      val pre = "lazy val"
      val llpre = if (LLs.isEmpty) "" else "resolveLL"
      val post = s"[$name] = memo(args => {" + LINE_SEP + (if (params.isEmpty) "" else {
        s"""    val List(${params.mkString(", ")}) = getArgsN("$name", args, ${params.length})""" + LINE_SEP
      }) + s"""    log($llpre("""
      nf.println(s"""  $pre $name: ESParser$post""")
      nf.print(noLLs.map {
        case (rhs, i) => getParsers(name, rhs.tokens, rhs.cond, i, false)
      }.mkString(" |" + LINE_SEP))
      if (!LLs.isEmpty) nf.print(LLs.map {
        case (rhs, i) => getParsers(name, rhs.tokens.drop(1), rhs.cond, i, true)
      }.mkString("," + LINE_SEP, " |" + LINE_SEP, ""))
      nf.println
      nf.println(s"""    ))("$name")""")
      nf.println(s"""  })""")
    }

    def getParsers(name: String, tokens: List[Token], cond: String, idx: Int, isSub: Boolean): String = {
      var parser = tokens.foldLeft("MATCH")(appendParser(_, _)) + " ^^ { case "
      val count = tokens.count(_ match {
        case (_: NonTerminal) | (_: ButNot) => true
        case _ => false
      })
      val ids = (0 until count).map("x" + _.toString)
      val astName = s"$name$idx"
      parser += s"_${ids.map(" ~ " + _).mkString("")} => " + (if (isSub) {
        s"""((x: $name) => $astName(x, ${ids.map(_ + ", ").mkString("")}args)) }"""
      } else {
        s"""$astName(${ids.map(_ + ", ").mkString("")}args) }"""
      })

      if (cond != "") parser = s"""(if (${cond}) ${parser} else MISMATCH)"""
      s"""      log($parser)("$astName")"""
    }

    lazy val paramMap: Map[String, List[String]] =
      prods.map(prod => prod.lhs.name -> prod.lhs.params).toMap
    def getArgs(name: String, args: List[String]): String = {
      val params = paramMap.getOrElse(name, Nil)
      val argMap = args.foldLeft(Map[String, String]()) {
        case (m, arg) =>
          val dropped = arg.drop(1)
          m + {
            if (arg.startsWith("~")) dropped -> "false"
            else if (arg.startsWith("+")) dropped -> "true"
            else if (arg.startsWith("?")) dropped -> ("p" + dropped)
            else dropped -> "false"
          }
      }
      s"""(List(${params.map(p => argMap.getOrElse(p, "false")).mkString(", ")}))"""
    }
    def appendParser(base: String, token: Token): String = token match {
      case Terminal(term) => s"""($base <~ t(${getTokenParser(token)}))"""
      case NonTerminal(name, args, optional) =>
        var parser = name
        if (lexNames contains parser) parser = s"""nt("$parser", $parser)"""
        else parser += getArgs(name, args)
        if (optional) parser = s"""opt($parser)"""
        s"""$base ~ $parser"""
      case ButNot(_, cases) =>
        val parser = getTokenParser(token)
        s"""$base ~ nt(\"\"\"$parser\"\"\", $parser)"""
      case Lookahead(contains, cases) =>
        val parser = cases.map(c => s"""ss(${
          c.map(token => {
            val t = getTokenParser(token)
            if (t.startsWith("\"") && t.endsWith("\"") && t(1).isLower) t + " <~ not(IDContinue)"
            else t
          }).mkString(", ")
        })""").mkString(" | ")
        val pre = if (contains) "+" else "-"
        s"""($base <~ ${pre}ntl($parser))"""
      case EmptyToken => base + " ~ MATCH"
      case NoLineTerminatorToken => s"""($base <~ NoLineTerminator)"""
      case _ => base
    }

    nf.println(s"""package $packageName.model""")
    nf.println
    nf.println(s"""import $packageName.AST""")
    nf.println(s"""import $packageName.core._""")
    nf.println(s"""import $packageName.parser.ESParsers""")
    nf.println
    nf.println(s"""object Parser extends ESParsers {""")
    lexProds.foreach(getStrParser)
    prods.foreach(getParser)
    nf.println(s"""  val TERMINAL: Lexer = (""")
    nf.println(terminalTokens.map(t => {
      if (t == "?.") s"""    "$t" <~ not(DecimalDigit)"""
      else s"""    "$t""""
    }).mkString(" |||" + LINE_SEP))
    nf.println(s"""  )""")
    nf.println(s"""  val rules: Map[String, ESParser[AST]] = Map(""")
    nf.println(prods.map {
      case Production(Lhs(name, _), _) => s""""$name" -> $name"""
    }.mkString("," + LINE_SEP))
    nf.println(s"""  )""")
    nf.println(s"""}""")
    nf.close()
  }
}
