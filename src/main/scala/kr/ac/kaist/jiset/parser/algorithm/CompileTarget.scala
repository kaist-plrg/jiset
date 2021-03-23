package kr.ac.kaist.jiset.parser.algorithm

import kr.ac.kaist.jiset.ir
import kr.ac.kaist.jiset.spec.algorithm.{ Token, Name }
import kr.ac.kaist.jiset.spec.grammar.Grammar
import kr.ac.kaist.jiset.util.Useful._
import org.jsoup.nodes.Document

class CompileTargets(val version: String) {
  val compiler: Compiler = Compiler(version)
  import compiler._

  sealed abstract class CompileTarget(
    val name: String,
    val parser: Parser[ir.IRNode]
  ) {
    // parse input
    def parse(
      code: List[String],
      secIds: Map[String, Name] = Map(),
      raw: Boolean = true
    )(implicit grammar: Grammar, document: Document): (List[Token], ParseResult[ir.IRNode]) = {
      // get tokens
      val tokens = if (raw) {
        // from raw string
        if (this eq InstsTarget) TokenParser.getTokens(code, secIds)
        else TokenParser.getTokens(code.mkString(" "), secIds, handleIndent = false)
      } else {
        // from tokens
        TokenParser.listFrom(code.mkString(" "))
      }
      (tokens, compiler.parseAll(parser, tokens))
    }

    def parseIR(str: String): ir.IRNode = this match {
      case TyTarget => ir.Parser.parseTy(str)
      case _ => ir.Parser.parseInst(str)
    }
  }
  object CompileTarget {
    val compiler = Compiler("recent")
    import compiler._
    val targets = List(
      InstsTarget, InstTarget, ExprTarget,
      ValTarget, CondTarget, TyTarget, RefTarget
    )
    val options = targets.map(t => "-" + t.name -> t).toMap
    def unapply(str: String): Option[CompileTarget] = options.get(str)
  }
  case object InstsTarget extends CompileTarget("insts", normalizedStmts)
  case object InstTarget extends CompileTarget("inst", stmt)
  case object ExprTarget extends CompileTarget("expr", expr2inst(expr))
  case object ValTarget extends CompileTarget("value", valueParser)
  case object CondTarget extends CompileTarget("cond", expr2inst(_cond))
  case object TyTarget extends CompileTarget("ty", ty)
  case object RefTarget extends CompileTarget("ref", ref2inst(ref))
}

object CompileTargets {
  private var versionMap: Map[String, CompileTargets] = Map()
  def apply(version: String): CompileTargets = versionMap.getOrElse(version, {
    val target = new CompileTargets(version)
    versionMap += version -> target
    target
  })
}
