package kr.ac.kaist.jiset.parser.algorithm

import Compiler._
import kr.ac.kaist.ires.ir
import kr.ac.kaist.jiset.spec.algorithm.Token
import kr.ac.kaist.jiset.spec.grammar.Grammar
import kr.ac.kaist.jiset.util.Useful._
import org.jsoup.nodes.Document

sealed abstract class CompileTarget(
    val name: String,
    val parser: Parser[ir.IRNode]
) {
  // parse input
  def parse(
    code: List[String],
    raw: Boolean = true
  )(implicit grammar: Grammar, document: Document): (List[Token], ParseResult[ir.IRNode]) = {
    // get tokens
    val tokens = if (raw) {
      // from raw string
      if (this eq InstsTarget) TokenParser.getTokens(code)
      else TokenParser.getTokens(code.mkString(" "), handleIndent = false)
    } else {
      // from tokens
      TokenParser.listFrom(code.mkString(" "))
    }
    (tokens, Compiler.parseAll(parser, tokens))
  }

  def parseIR(str: String): ir.IRNode = this match {
    case InstsTarget => ir.Parser.parseInst(str)
    case InstTarget => ir.Parser.parseInst(str)
    case ExprTarget => ir.Parser.parseInst(str)
    case ValTarget => ir.Parser.parseValue(str)
    case CondTarget => ir.Parser.parseInst(str)
    case TyTarget => ir.Parser.parseTy(str)
    case RefTarget => ir.Parser.parseInst(str)
  }
}
object CompileTarget {
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
case object CondTarget extends CompileTarget("cond", expr2inst(cond))
case object TyTarget extends CompileTarget("ty", ty)
case object RefTarget extends CompileTarget("ref", ref2inst(ref))
