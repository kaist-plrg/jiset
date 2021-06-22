package kr.ac.kaist.jiset.spec

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.ir
import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Appender._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.spec.algorithm.{ token => atoken, _ }
import kr.ac.kaist.jiset.spec.grammar.{ token => gtoken, _ }

// ECMAScript Beautifier
object Beautifier {
  val irBeautifier = new ir.Beautifier(index = true)
  import irBeautifier._

  // ECMAScript components
  implicit lazy val SpecComponentApp: App[SpecComponent] = (app, node) => node match {
    case comp: ECMAScript => ECMAScriptApp(app, comp)
    case comp: Section => SectionApp(app, comp)
    case comp: Algo => AlgoApp(app, comp)
    case comp: Head => HeadApp(app, comp)
    case comp: Param => ParamApp(app, comp)
    case comp: atoken.Token => AlgoTokenApp(app, comp)
    case comp: Grammar => GrammarApp(app, comp)
    case comp: Lhs => LhsApp(app, comp)
    case comp: Production => ProductionApp(app, comp)
    case comp: Rhs => RhsApp(app, comp)
    case comp: gtoken.Token => GrammarTokenApp(app, comp)
  }

  // ECMAScript
  implicit lazy val ECMAScriptApp: App[ECMAScript] = (app, spec) => {
    val ECMAScript(version, grammar, algos, consts, intrinsics, symbols, aoids, section) = spec
    app >> "* version: " >> version >> LINE_SEP
    app >> "* grammar:" >> LINE_SEP
    app >> "  - lexical production: " >> grammar.lexProds.length >> LINE_SEP
    app >> "  - non-lexical production: " >> grammar.prods.length >> LINE_SEP
    app >> "* algorithms:" >> LINE_SEP
    app >> "  - incomplete: " >> spec.incompletedAlgos.length >> LINE_SEP
    app >> "  - complete: " >> spec.completedAlgos.length >> LINE_SEP
    app >> "  - total: " >> algos.length >> LINE_SEP
    app >> "* consts: " >> consts.size >> LINE_SEP
    app >> "* intrinsics: " >> intrinsics.size >> LINE_SEP
    app >> "* symbols: " >> symbols.size >> LINE_SEP
    app >> "* aoids: " >> aoids.size >> LINE_SEP
    val sum = spec.incompletedAlgos.map(_.todos.length).sum
    app >> "* incompleted steps: " >> sum >> LINE_SEP
  }

  // section
  implicit lazy val SectionApp: App[Section] = (app, section) => {
    app >> "[" >> section.id >> "] "
    app.listWrap(section.subs)
  }

  // algorithm
  implicit lazy val AlgoApp: App[Algo] = (app, algo) => {
    val Algo(head, id, rawBody, code) = algo
    app >> "def " >> head >> " = " >> rawBody >> LINE_SEP
    app >> "- id: " >> id >> LINE_SEP
    app >> "- code:" >> LINE_SEP
    implicit lazy val c = ListApp[String](sep = LINE_SEP)
    app >> code.toList
  }

  // algorithm head
  implicit lazy val HeadApp: App[Head] = (app, head) => {
    implicit val p = ListApp[Param]("(", ", ", ")")
    val name = head.name
    head match {
      case NormalHead(_, params) =>
        app >> name >> params
      case MethodHead(_, _, receiverParam, origParams) =>
        app >> "[METHOD] " >> name >> List(receiverParam) >> origParams
      case SyntaxDirectedHead(_, _, _, rhs, _, withParams) =>
        app >> "[SYNTAX] " >> name >> "<" >> rhs >> ">" >> withParams
      case BuiltinHead(ref, origParams) =>
        app >> "[BUILTIN] " >> ref >> origParams
    }
  }

  // algorithm parameter
  implicit lazy val ParamApp: App[Param] = (app, param) => {
    import Param.Kind._
    val Param(name, kind) = param
    kind match {
      case Normal => app >> name
      case Optional => app >> name >> "?"
      case Variadic => app >> "..." >> name
    }
  }

  // algorithm token
  implicit lazy val AlgoTokenApp: App[atoken.Token] = (app, token) => {
    import atoken._
    val Token(name, content) = token
    token match {
      case Text(t) => app >> t
      case _ => app >> name >> ":{" >> content >> "}"
    }
  }

  // grammar
  implicit lazy val GrammarApp: App[Grammar] = (app, grammar) => {
    val DOUBLE_LS = LINE_SEP * 2
    val (lprods, sprods) = grammar.sortedProds
    implicit val l = ListApp[Production](sep = DOUBLE_LS)
    app >> Grammar.lexicalHeader >> DOUBLE_LS
    app >> lprods >> DOUBLE_LS
    app >> Grammar.syntacticHeader >> DOUBLE_LS
    app >> sprods
  }

  // grammar left-hand-side
  implicit lazy val LhsApp: App[Lhs] = (app, lhs) => {
    val Lhs(name, params) = lhs
    implicit val s = ListApp[String]("[", ", ", "]")
    app >> name
    if (!params.isEmpty) app >> params else app
  }

  // grammar production
  implicit lazy val ProductionApp: App[Production] = (app, prod) => {
    val Production(lhs, rhsList) = prod
    app >> lhs >> ": "
    app.listWrap(rhsList)
  }

  // grammar right-hand-side
  implicit lazy val RhsApp: App[Rhs] = (app, rhs) => {
    val Rhs(tokens, condOpt) = rhs
    implicit val t = ListApp[gtoken.Token](sep = " ")
    condOpt.foreach(_ match {
      case RhsCond(name, true) => app >> "[+" >> name >> "] "
      case RhsCond(name, false) => app >> "[~" >> name >> "] "
    })
    app >> tokens
  }

  // grammar token
  implicit lazy val GrammarTokenApp: App[gtoken.Token] = (app, token) => {
    import gtoken._
    token match {
      case Terminal(term) =>
        app >> "`" >> term >> "`"
      case NonTerminal(name, args, optional) =>
        app >> name
        implicit val a = ListApp[String]("[", ", ", "]")
        if (!args.isEmpty) app >> args
        app >> (if (optional) "?" else "")
      case ButNot(base, cases) =>
        implicit val t = ListApp[Token](sep = " or ")
        app >> base >> " but not " >> cases
      case Lookahead(contains, cases) =>
        implicit val c = ListApp[Token](sep = " ")
        implicit val l = ListApp[List[Token]]("{", ", ", "}")
        app >> "[lookahead " >> (if (contains) "<" else "<!") >> " " >> cases >> "]"
      case EmptyToken =>
        app >> "[empty]"
      case NoLineTerminatorToken =>
        app >> "[no LineTerminator here]"
      case (char: Character) =>
        app >> "<" >> char.name >> ">"
    }
  }
}
