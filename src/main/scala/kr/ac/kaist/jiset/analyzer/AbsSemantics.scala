package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.analyzer.domain.Beautifier._
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.spec.algorithm.{ Algo, Head, SyntaxDirectedHead }
import kr.ac.kaist.jiset.util.Useful._
import scala.Console.CYAN
import scala.util.matching.Regex

class AbsSemantics(val cfg: CFG) {
  // ECMAScript
  val spec: ECMAScript = cfg.spec

  // internal map from control points to abstract states
  private var npMap: Map[NodePoint, AbsState] = initNpMap
  private var rpMap: Map[ReturnPoint, (AbsHeap, AbsValue)] = Map()

  // worklist
  val worklist: Worklist[ControlPoint] = new StackWorklist(npMap.keySet)

  // global variables and heaps
  val (globalVars, globalHeaps): (Map[String, AbsValue], Map[Addr, AbsObj]) = {
    val (vars, heaps) = cfg.getGlobal
    val globalVars = manualVars ++ (for ((x, v) <- vars) yield x -> AbsValue(v))
    var globalHeaps: Map[Addr, AbsObj] = (for ((x, m) <- manualHeaps) yield {
      val map: Map[String, AbsObj.MapD.AbsVOpt] = m.map {
        case (k, vs) => k -> AbsObj.MapD.AbsVOpt(AbsValue(vs: _*), AbsAbsent.Bot)
      }
      NamedAddr(x) -> AbsObj.Elem(map = AbsObj.MapD(map, AbsObj.MapD.AbsVOpt(None)))
    }).toMap
    globalHeaps ++= (for ((a, o) <- heaps) yield a -> AbsObj(o))
    (globalVars, globalHeaps)
  }
  private def manualVars: Map[String, AbsValue] = Map(
    "GLOBAL_context" -> AbsValue(NamedAddr("ExecutionContext")),
  )
  private def manualHeaps: Map[String, Map[String, List[Value]]] = Map(
    "ExecutionContext" -> Map(
      "LexicalEnvironment" -> List(NamedAddr("EnvironmentRecord")),
    ),
    "EnvironmentRecord" -> Map(
      "HasThisBinding" -> getClos(""".*\.HasThisBinding""".r),
      "ThisBindingStatus" -> getConsts("lexical", "initialized", "uninitialized"),
    ),
  )
  private def getClos(pattern: Regex): List[Clo] = for {
    func <- cfg.funcs.toList
    if pattern.matches(func.algo.head.printName)
  } yield Clo(func.uid, Env())
  private def getConsts(names: String*): List[Addr] =
    names.toList.map(x => NamedAddr("CONST_" + x))

  //////////////////////////////////////////////////////////////////////////////
  // Helper Functions
  //////////////////////////////////////////////////////////////////////////////
  // lookup
  def apply(np: NodePoint): AbsState = npMap.getOrElse(np, AbsState.Bot)
  def apply(rp: ReturnPoint): (AbsHeap, AbsValue) =
    rpMap.getOrElse(rp, (AbsHeap.Bot, AbsValue.Bot))

  // update internal map
  def +=(pair: (NodePoint, AbsState)): Unit = {
    val (np, newSt) = pair
    val oldSt = this(np)
    if (!(newSt ⊑ oldSt)) {
      npMap += np -> (oldSt ⊔ newSt)
      worklist += np
    }
  }
  def doReturn(pair: (ReturnPoint, (AbsHeap, AbsValue))): Unit = {
    val (rp, (newH, newV)) = pair
    val (oldH, oldV) = this(rp)
    if (!(newH ⊑ oldH && newV ⊑ oldV)) {
      rpMap += rp -> (oldH ⊔ newH, oldV ⊔ newV)
      worklist += rp
    }
  }

  // get function of given control points
  def funcOf(cp: ControlPoint): Function = cp match {
    case NodePoint(node, _) => cfg.funcOf(node)
    case ReturnPoint(func, _) => func
  }

  // conversion to string
  override def toString: String =
    rpMap.keySet.toList.map(getString).sorted.mkString(LINE_SEP)

  // get string for result of control points
  def getString(cp: ControlPoint): String = {
    val cyan = setColor(CYAN)
    val (k, v) = cp match {
      case np @ NodePoint(entry: Entry, view) =>
        val st = this(np)
        (cyan(s"${cfg.funcOf(entry).name}:$view:ENTRY"), beautify(st))
      case (np: NodePoint) =>
        val st = this(np)
        (np.toString, beautify(st))
      case (rp: ReturnPoint) =>
        val (h, v) = this(rp)
        (cyan(s"$rp:RETURN"), beautify(v) + (
          if (h.isBottom) ""
          else s" @ ${beautify(h)}"
        ))
    }
    s"$k -> $v"
  }

  //////////////////////////////////////////////////////////////////////////////
  // Private Helper Functions
  //////////////////////////////////////////////////////////////////////////////
  // initialization of node points with abstract states
  private def initNpMap: Map[NodePoint, AbsState] = (for {
    func <- cfg.funcs.toList
    (types, st) <- getTypes(func.algo.head)
    view = View(types)
    cp = NodePoint(func.entry, view)
  } yield cp -> st).toMap

  // target algorithms
  private def successPatterns = List(
    // Success
    """Literal\[.*""".r,
    """PrimaryExpression.*IsIdentifierRef""".r,
    // .IsFunctionDefinition
    """Expression\[1,0\].IsFunctionDefinition""".r,
    """PrimaryExpression\[[0-4],0\].IsFunctionDefinition""".r,
    """PrimaryExpression\[1[0-1],0\].IsFunctionDefinition""".r, // [0,0]~[11,0]
    """UpdateExpression\[.*.IsFunctionDefinition""".r,
    """UnaryExpression\[.*.IsFunctionDefinition""".r,
    """ExponentiationExpression\[.*.IsFunctionDefinition""".r,
    """MultiplicativeExpression\[.*.IsFunctionDefinition""".r,
    """AdditiveExpression\[.*.IsFunctionDefinition""".r,
    """ShiftExpression\[.*.IsFunctionDefinition""".r,
    """RelationalExpression\[.*.IsFunctionDefinition""".r,
    """EqualityExpression\[.*.IsFunctionDefinition""".r,
    """BitWiseANDExpression\[.*.IsFunctionDefinition""".r,
    """BitWiseXORExpression\[.*.IsFunctionDefinition""".r,
    """BitWiseORExpression\[.*.IsFunctionDefinition""".r,
    """LogicalANDExpression\[.*.IsFunctionDefinition""".r,
    """LogicalORExpression\[.*.IsFunctionDefinition""".r,
    """CoalesceExpression\[.*.IsFunctionDefinition""".r,
    """ConditionalExpression\[.*.IsFunctionDefinition""".r,
    """AssignmentExpression\[.*.IsFunctionDefinition""".r,
    """FunctionExpression\[.*.IsFunctionDefinition""".r,
    """GeneratorExpression\[.*.IsFunctionDefinition""".r,
    """AsyncGeneratorExpression\[.*.IsFunctionDefinition""".r,
    """ClassExpression\[.*.IsFunctionDefinition""".r,
    """AsyncFunctionExpression\[.*.IsFunctionDefinition""".r,
    """Expression\[1,0\].AssignmentTargetType""".r,
    // String Value
    """IdentifierReference\[1,0\].StringValue""".r,
    """BindingIdentifier\[.*.StringValue""".r,
    """LabelIdentifier\[.*.StringValue""".r,
    """StringLiteral\[.*.StringValue""".r,
    """IdentifierReference\[.*.StringValue""".r,
    // Etc.
    """LiteralPropertyName\[1,0\].PropName""".r,
    """LiteralPropertyName\[1,0\].Evaluation""".r,
  )

  private def failedPatterns = List(
    // need implemetation of IAccess, IApp
    """PrimaryExpression\[0,0\].Evaluation""".r,
    """PrimaryExpression\[12,0\].Evaluation""".r,
    """NewExpression\[1,0\].Evaluation""".r,
    """PrimaryExpression\[12,0\].IsFunctionDefinition""".r,
    // not implemented `app`
    """YieldExpression\[0,0\].Evaluation""".r,
    // not implemented `access`
    """Identifier\[.*.StringValue""".r,
    """PropertyDefinition\[0,0\].PropName""".r,
    """StringLiteral\[0,1\].StringValue""".r,
    // not implemented transfer for `RefValue `
    """IdentifierReference\[0,0\].AssignmentTargetType""".r,
    // has parameter
    """TemplateSpans\[0,0\].SubstitutionEvaluation""".r,
    // not implemented: `return (new [])`
    """TemplateLiteral\[0,0\].TemplateStrings""".r,
    // not implemented: access Identifier "StringValue" -> maybe abstract StringValue to `str`?
    """Identifier\[0,0\].StringValue""".r,
  )

  private def targetPatterns = List(
    """PrimaryExpression\[0,0\].Evaluation""".r,
  )

  private def isTarget(head: SyntaxDirectedHead): Boolean = (
    head.withParams.isEmpty &&
    targetPatterns.exists(_.matches(head.printName))
  )

  // initial abstract state for syntax-directed algorithms
  private def getTypes(head: Head): List[(List[Type], AbsState)] = head match {
    case (head: SyntaxDirectedHead) if isTarget(head) => head.optional.subsets.map(opt => {
      var st = AbsState.Empty
      val types: List[Type] = head.types.map {
        case (name, _) if opt contains name =>
          st += name -> AbsAbsent.Top
          AbsentT
        case (name, astName) =>
          st += name -> AbsAST(ASTVal(astName))
          AstT(astName)
      }
      (types, st)
    }).toList
    case _ => Nil
  }
}
