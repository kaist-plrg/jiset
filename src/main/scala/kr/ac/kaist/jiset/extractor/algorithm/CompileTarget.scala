package kr.ac.kaist.jiset.extractor.algorithm

import kr.ac.kaist.jiset.ir
import kr.ac.kaist.jiset.spec.algorithm.token.{ Tokens, Token }
import kr.ac.kaist.jiset.spec.grammar.Grammar
import kr.ac.kaist.jiset.util.Useful._
import org.jsoup.nodes.Document

class CompileTargets(val version: String, secIds: Map[String, String]) {
  val compiler = Compiler(version, secIds)
  import compiler._

  val targets = List(
    InstsTarget, InstTarget, ExprTarget,
    ValTarget, CondTarget, TyTarget, RefTarget
  )

  val options = targets.map(t => "-" + t.name -> t).toMap

  object CompileTarget {
    def unapply(str: String): Option[CompileTarget] = options.get(str)
  }

  sealed abstract class CompileTarget(
    val name: String,
    val parser: Parser[ir.IRComponent]
  ) {
    // parse input
    def parse(
      code: List[String],
      raw: Boolean = true
    )(implicit grammar: Grammar, document: Document): (List[Token], ParseResult[ir.IRComponent]) = {
      // get tokens
      val tokens = if (raw) {
        // from raw string
        if (this eq InstsTarget) TokenParser.getTokens(code)
        else TokenParser.getTokens(code.mkString(" "), handleIndent = false)
      } else {
        Tokens(code.mkString(" "))
      }
      (tokens, compiler.parseAll(parser, tokens))
    }

    def parseIR(str: String): ir.IRComponent = this match {
      case TyTarget => ir.Ty(str)
      case _ => ir.Inst(str)
    }
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
  def apply(version: String, secIds: Map[String, String]): CompileTargets = versionMap.getOrElse(version, {
    val target = new CompileTargets(version, secIds)
    versionMap += version -> target
    target
  })
}
