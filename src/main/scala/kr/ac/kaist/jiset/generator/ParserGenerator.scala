package kr.ac.kaist.jiset.generator

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.spec.grammar._
import kr.ac.kaist.jiset.util.Useful._

case class ParserGenerator(grammar: Grammar, modelDir: String) {
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
  val PREDEF_LEXER = List(
    "WhiteSpace".r,
    "LineTerminator".r,
    "LineTerminatorSequence".r,
    ".*Comment.*".r
  )
  val paramMap: Map[String, List[String]] =
    prods.map(prod => prod.lhs.name -> prod.lhs.params).toMap
  val nf = getPrintWriter(s"$modelDir/Parser.scala")
  generate
  nf.close()

  private def generate: Unit = {
    nf.println(s"""package $IRES_PACKAGE.model""")
    nf.println
    nf.println(s"""import $IRES_PACKAGE.ir._""")
    nf.println(s"""import $IRES_PACKAGE.parser.ESParsers""")
    nf.println(s"""import $IRES_PACKAGE.util.Span""")
    nf.println
    nf.println(s"""object Parser extends ESParsers {""")
    lexProds.filter(isTargetLexer).foreach(genLexer)
    prods.foreach(genParser)
    nf.println(s"""  val TERMINAL: Lexer = (""")
    nf.println(terminalTokens.map(t => {
      if (t == "?.") s"""    "$t" <~ not(DecimalDigit)"""
      else s"""    "$t""""
    }).mkString(" |||" + LINE_SEP))
    nf.println(s"""  )""")
    nf.println(s"""  val rules: Map[String, ESParser[AST]] = Map(""")
    nf.println(prods.map {
      case Production(Lhs(name, _), _) => s"""    "$name" -> $name"""
    }.mkString("," + LINE_SEP))
    nf.println(s"""  )""")
    nf.println(s"""}""")
  }
  private def isTargetLexer(prod: Production): Boolean = (
    !PREDEF_LEXER.exists(_.matches(prod.name)) &&
    !Grammar.isExtNT(prod.name)
  )

  private def genLexer(prod: Production): Unit = {
    val Production(lhs, rhsList) = prod
    val name = lhs.name

    nf.println(s"""  lazy val $name: Lexer = (""")
    nf.print(rhsList.map {
      case rhs => s"""    ${rhs.tokens.map(getTokenParser).mkString(" % ")}"""
    }.mkString(" |||" + LINE_SEP))
    nf.println
    nf.println(s"""  )""")
  }

  private def getTokenParser(token: Token): String = token match {
    case Terminal(term) => s""""${normStr(term)}""""
    case NonTerminal(name, args, optional) =>
      if (optional) s"""$name.opt"""
      else name
    case ButNot(base, cases) =>
      val parser = getTokenParser(base)
      val notParser = cases.map(token => getTokenParser(token)).mkString("(", " ||| ", ")")
      s"""($parser \\ $notParser)"""
    case Lookahead(contains, cases) =>
      val parser = cases.map(c => s"""(${c.map(token => getTokenParser(token)).mkString(" % ")})""").mkString(" ||| ")
      if (contains) s"""+$parser"""
      else s"""-$parser"""
    case EmptyToken => "EMPTY"
    case NoLineTerminatorToken => "strNoLineTerminator"
    case Unicode(code) => code
    case UnicodeAny => "Unicode"
    case UnicodeIdStart => "IDStart"
    case UnicodeIdContinue => "IDContinue"
    case (c: Character) => "HexDigits"
  }

  private def genParser(prod: Production): Unit = {
    val Production(lhs, rhsList) = prod
    val Lhs(name, rawParams) = lhs
    val params = rawParams.map("p" + _)

    val noLRs = rhsList.zipWithIndex.filter { case (rhs, _) => !isLR(name, rhs) }
    val LRs = rhsList.zipWithIndex.filter { case (rhs, _) => isLR(name, rhs) }

    val pre = "lazy val"
    val llpre = if (LRs.isEmpty) "" else "resolveLR("
    val post = s"[$name] = memo(args => {" + LINE_SEP + (if (params.isEmpty) "" else {
      s"""    val List(${params.mkString(", ")}) = getArgsN("$name", args, ${params.length})""" + LINE_SEP
    }) + s"""    log($llpre("""
    nf.println(s"""  $pre $name: ESParser$post""")
    nf.print(noLRs.map {
      case (rhs, i) => genParsers(name, rhs.tokens, rhs.condOpt, i, false)
    }.mkString(" |" + LINE_SEP))
    if (!LRs.isEmpty) nf.print(LRs.map {
      case (rhs, i) => genParsers(name, rhs.tokens.drop(1), rhs.condOpt, i, true)
    }.mkString(LINE_SEP + "    ), (" + LINE_SEP, " |" + LINE_SEP, ""))
    nf.println
    nf.println("    " + (if (LRs.isEmpty) "" else ")") + s"""))("$name")""")
    nf.println(s"""  })""")
  }

  private def genParsers(
    name: String,
    tokens: List[Token],
    condOpt: Option[RhsCond],
    idx: Int,
    isSub: Boolean
  ): String = {
    var parser = tokens.foldLeft("MATCH")(appendParser(_, _)) + " ^^ { case "
    val count = tokens.count(_ match {
      case (_: NonTerminal) | (_: ButNot) => true
      case _ => false
    })
    val ids = (0 until count).map("x" + _.toString)
    val astName = s"$name$idx"
    val args = ids ++ List("args", "Span()") // TODO span info
    parser += s"_${ids.map(" ~ " + _).mkString("")} => " + (if (isSub) {
      s"""((x: $name) => $astName(x, ${args.mkString(", ")})) }"""
    } else {
      s"""$astName(${args.mkString(", ")}) }"""
    })
    condOpt.foreach(cond => parser = s"(if ($cond) $parser else MISMATCH)")
    s"""      log($parser)("$astName")"""
  }

  private def appendParser(base: String, token: Token): String = token match {
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
      val parser = cases.map(c => s"""(${
        c.map(token => {
          val t = getTokenParser(token)
          if (t.startsWith("\"") && t.endsWith("\"") && t(1).isLower) "(" + t + " <~ not(IDContinue))"
          else t
        }).mkString(" %% ")
      })""").mkString(" | ")
      val pre = if (contains) "+" else "-"
      s"""($base <~ ${pre}ntl($parser))"""
    case EmptyToken => base + " ~ MATCH"
    case NoLineTerminatorToken => s"""($base <~ NoLineTerminator)"""
    case _ => base
  }

  private def isLR(name: String, rhs: Rhs): Boolean = rhs.tokens match {
    case NonTerminal(`name`, _, _) :: _ => true
    case _ => false
  }

  private def getArgs(name: String, args: List[String]): String = {
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
}
