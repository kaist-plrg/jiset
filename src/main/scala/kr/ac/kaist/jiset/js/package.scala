package kr.ac.kaist.jiset

import kr.ac.kaist.jiset.extractor.ECMAScriptParser
import kr.ac.kaist.jiset.cfg.CFG
import kr.ac.kaist.jiset.ir.{ Parser => IRParser, _ }
import kr.ac.kaist.jiset.js.ast._
import kr.ac.kaist.jiset.spec.JsonProtocol._
import kr.ac.kaist.jiset.spec.{ Parser => SpecParser, _ }
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util.JvmUseful._

package object js {
  // set current ECMAScript model
  def setSpec(spec: => ECMAScript): Unit =
    if (_spec.isEmpty) _spec = Some(spec)
  private var _spec: Option[ECMAScript] = None

  // current ECMAScript model
  lazy val spec: ECMAScript = _spec.get

  // current control-flow graph (CFG)
  lazy val cfg: CFG = new CFG(spec)

  // json protocols
  lazy val cfgJsonProtocol = cfg.jsonProtocol
  lazy val checkerJsonProtocol = new checker.JsonProtocol(cfg)

  // ECMAScript components
  lazy val intrinsics: Set[String] = spec.intrinsics
  lazy val symbols: Set[String] = spec.symbols
  lazy val algoMap: Map[String, Algo] = spec.algoMap

  // default algorithm for `Contains` static semantics
  lazy val defaultContains: Algo = algoMap.getOrElse("Contains", {
    error("cannot find default `Contains` algorithm")
  })

  // conversion intrinsics to address
  val intrinsicRegex = "%([^%]+)%".r
  def intrinsicToAddr(name: String): Addr = {
    val newName = name
      .replaceAll("Function.prototype.prototype", ".prototype")
    NamedAddr(GLOBAL + "." + newName)
  }

  // constants
  val AGENT = "AGENT"
  val ALGORITHM = "ALGORITHM"
  val CONTEXT = "CONTEXT"
  val EXECUTION_STACK = "EXECUTION_STACK"
  val GLOBAL = "GLOBAL"
  val HOST_DEFINED = "HOST_DEFINED"
  val INTRINSICS = "INTRINSICS"
  val JOB_QUEUE = "JOB_QUEUE"
  val PRIMITIVE = "PRIMITIVE"
  val REALM = "REALM"
  val RESULT = "RESULT"
  val RETURN = "RETURN"
  val RET_CONT = "RET_CONT"
  val SCRIPT_BODY = "SCRIPT_BODY"
  val SYMBOL_REGISTRY = "SYMBOL_REGISTRY"
  val TOP_LEVEL = "RunJobs"
  val TYPED_ARRAY_INFO = "TYPED_ARRAY_INFO"

  // prefixes
  val SYMBOL_PREFIX = "SYMBOL_"

  // get argument list from Arguments
  def getArguments(ast: Arguments): List[AssignmentExpression] = ast match {
    case Arguments0(_, _) => List()
    case Arguments1(x1, _, _) => _getArguments(x1)
    case Arguments2(x1, _, _) => _getArguments(x1)
  }
  def _getArguments(ast: ArgumentList): List[AssignmentExpression] = ast match {
    case ArgumentList0(x0, _, _) => List(x0)
    case ArgumentList1(x0, _, _) => List(x0)
    case ArgumentList2(x0, x2, _, _) => _getArguments(x0) ++ List(x2)
    case ArgumentList3(x0, x2, _, _) => _getArguments(x0) ++ List(x2)
  }

  // flatten statements
  def flattenStmtList(
    s: StatementList,
    list: List[StatementListItem] = Nil
  ): List[StatementListItem] = s match {
    case StatementList0(x0, _, _) => x0 :: list
    case StatementList1(x0, x1, _, _) => flattenStmtList(x0, x1 :: list)
  }
  def flattenStmt(s: Script): List[StatementListItem] = s match {
    case Script0(Some(ScriptBody0(stlist, _, _)), _, _) =>
      flattenStmtList(stlist)
    case _ => Nil
  }

  // merge statements to script
  def mergeStmtList(
    l: List[StatementListItem],
    params: List[Boolean]
  ): Option[StatementList] =
    l match {
      case a :: rest => {
        val init: StatementList = StatementList0(a, params, a.span)
        val list = rest.foldLeft(init) {
          case (x, y) =>
            val span = Span(x.span.start, y.span.end)
            StatementList1(x, y, params, span)
        }
        Some(list)
      }
      case Nil => None
    }
  def mergeStmt(l: List[StatementListItem]): Script = {
    val params = List(false, false, false)
    val bodyOpt = mergeStmtList(l, params).map(l => ScriptBody0(l, params, l.span))
    val span = bodyOpt.fold(Span())(_.span)
    Script0(bodyOpt, params, span)
  }

  // flatten expressions
  def flattenExprList(
    s: Expression,
    list: List[AssignmentExpression] = Nil
  ): List[AssignmentExpression] = s match {
    case Expression0(x0, _, _) => x0 :: list
    case Expression1(x0, x2, _, _) => flattenExprList(x0, x2 :: list)
  }
  def flattenExpr(
    s: CoverParenthesizedExpressionAndArrowParameterList
  ): (List[AssignmentExpression], List[Boolean], Span) = s match {
    case CoverParenthesizedExpressionAndArrowParameterList0(expr, ps, span) =>
      (flattenExprList(expr), ps, span)
    case _ => (Nil, Nil, Span())
  }

  // merge statements to Expression
  def mergeExprList(
    l: List[AssignmentExpression],
    params: List[Boolean]
  ): Option[Expression] =
    l match {
      case a :: rest => {
        val init: Expression = Expression0(a, params, a.span)
        val list = rest.foldLeft(init) {
          case (x, y) =>
            val span = Span(x.span.start, y.span.end)
            Expression1(x, y, params, span)
        }
        Some(list)
      }
      case Nil => None
    }
  def mergeExpr(
    l: List[AssignmentExpression],
    params: List[Boolean],
    span: Span
  ): CoverParenthesizedExpressionAndArrowParameterList = {
    val exprOpt = mergeExprList(l, params)
    exprOpt match {
      case Some(expr) => CoverParenthesizedExpressionAndArrowParameterList0(expr, params, span)
      case None => CoverParenthesizedExpressionAndArrowParameterList2(params, span)
    }
  }

  // flatten Property Definition List
  def flattenPdList(
    s: PropertyDefinitionList,
    list: List[PropertyDefinition] = Nil
  ): List[PropertyDefinition] = s match {
    case PropertyDefinitionList0(x0, _, _) => x0 :: list
    case PropertyDefinitionList1(x0, x2, _, _) => flattenPdList(x0, x2 :: list)
  }
  def flattenPd(
    s: ObjectLiteral
  ): (List[PropertyDefinition], List[Boolean], Span) = s match {
    case ObjectLiteral1(pds, ps, span) =>
      (flattenPdList(pds), ps, span)
    case _ => (Nil, Nil, Span())
  }

  // merge Property Definitions to script
  def mergePdList(
    l: List[PropertyDefinition],
    params: List[Boolean]
  ): Option[PropertyDefinitionList] =
    l match {
      case a :: rest => {
        val init: PropertyDefinitionList = PropertyDefinitionList0(a, params, a.span)
        val list = rest.foldLeft(init) {
          case (x, y) =>
            val span = Span(x.span.start, y.span.end)
            PropertyDefinitionList1(x, y, params, span)
        }
        Some(list)
      }
      case Nil => None
    }
  def mergePd(
    l: List[PropertyDefinition],
    params: List[Boolean],
    span: Span
  ): ObjectLiteral = {
    val pdOpt = mergePdList(l, params)
    pdOpt match {
      case Some(pd) => ObjectLiteral1(pd, params, span)
      case None => ObjectLiteral0(params, span)
    }
  }
  // parse js file
  def parseJsFile(filename: String): Script =
    Parser.parse(Parser.Script(Nil), fileReader(filename)).get
}
