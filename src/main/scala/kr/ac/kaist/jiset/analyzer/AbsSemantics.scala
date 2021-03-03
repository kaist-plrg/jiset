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
  private var npMap: Map[NodePoint[_], AbsState] = initNpMap
  private var rpMap: Map[ReturnPoint, (AbsHeap, AbsValue)] = Map()

  // internal map for return edges
  private var retEdges: Map[ReturnPoint, Set[(NodePoint[Call], String)]] = Map()

  // worklist
  lazy val worklist: Worklist[ControlPoint] = new StackWorklist(npMap.keySet)

  // global variables and heaps
  lazy val (globalVars, globalHeaps): (Map[String, AbsValue], Map[Addr, AbsObj]) = {
    val (vars, heaps) = cfg.getGlobal
    val globalVars = manualVars ++ (for ((x, v) <- vars) yield x -> AbsValue(v))
    var globalHeaps: Map[Addr, AbsObj] = (for ((x, m) <- manualHeaps) yield {
      val map: Map[String, AbsObj.MapD.AbsVOpt] = m.map {
        case (k, v) => k -> AbsObj.MapD.AbsVOpt(v, AbsAbsent.Bot)
      }
      NamedAddr(x) -> AbsObj.MapElem(None, AbsObj.MapD(map, AbsObj.MapD.AbsVOpt(None)))
    }).toMap
    globalHeaps ++= (for ((a, o) <- heaps) yield a -> AbsObj(o))
    (globalVars, globalHeaps)
  }
  private def manualVars: Map[String, AbsValue] = Map(
    "GLOBAL_context" -> AbsValue(NamedAddr("ExecutionContext")),
  )
  private def manualHeaps: Map[String, Map[String, AbsValue]] = Map(
    "ExecutionContext" -> Map(
      "LexicalEnvironment" -> AbsValue(NamedAddr("EnvironmentRecord")),
    ),
    "EnvironmentRecord" -> Map(
      "HasThisBinding" -> getClos(""".*\.HasThisBinding""".r),
      "GetThisBinding" -> getClos(""".*\.GetThisBinding""".r),
      "ThisBindingStatus" -> getConsts("lexical", "initialized", "uninitialized"),
      "OuterEnv" -> AbsValue(NamedAddr("EnvironmentRecord"), Null),
      "GlobalThisValue" -> AbsValue(NamedAddr("Global")),
      "ThisValue" -> ESValue,
    ),
    "Global" -> Map(),
    "Object" -> Map(),
  )
  private val ESValue: AbsValue = {
    val prim = AbsPrim.Top.copy(absent = AbsAbsent.Bot)
    AbsPure(addr = AbsAddr(NamedAddr("Object")), prim = prim)
  }
  private def getClos(pattern: Regex): AbsValue = AbsValue(for {
    func <- cfg.funcs.toSet
    if pattern.matches(func.algo.head.printName)
  } yield (Clo(func.uid, Env()): Value))
  private def getConsts(names: String*): AbsValue =
    AbsValue(names.toSet.map[Value](Const(_)))

  //////////////////////////////////////////////////////////////////////////////
  // Helper Functions
  //////////////////////////////////////////////////////////////////////////////
  // lookup
  def apply(np: NodePoint[_]): AbsState = npMap.getOrElse(np, AbsState.Bot)
  def apply(rp: ReturnPoint): (AbsHeap, AbsValue) =
    rpMap.getOrElse(rp, (AbsHeap.Bot, AbsValue.Bot))
  def _getRetEdges = retEdges
  def getRetEdges(rp: ReturnPoint): Set[(NodePoint[Call], String)] =
    retEdges.getOrElse(rp, Set())
  def getControlPoints: Set[ControlPoint] =
    npMap.keySet ++ rpMap.keySet

  // update internal map
  def +=[T <: Node](pair: (NodePoint[T], AbsState)): Unit = {
    val (np, newSt) = pair
    val oldSt = this(np)
    if (!(newSt ⊑ oldSt)) {
      npMap += np -> (oldSt ⊔ newSt)
      worklist += np
    }
  }

  // handle calls
  def doCall(
    call: Call,
    callView: View,
    st: AbsState,
    f: AbsClo,
    args: List[AbsValue],
    retVar: String
  ): Unit = for {
    ts <- getTypes(st, args)
    view = View(ts)
    pair <- f
  } {
    val AbsClo.Pair(fid, _) = pair // TODO handle envrionments
    val func = cfg.fidMap(fid)
    val pairs = func.algo.params.map(_.name) zip args
    val np = NodePoint(func.entry, view)
    val newSt = pairs.foldLeft(st.copy(env = AbsEnv.Empty)) {
      case (st, (x, v)) => st + (x -> v)
    }
    this += np -> newSt

    val rp = ReturnPoint(func, view)
    val callNP = NodePoint(call, callView)
    val set = retEdges.getOrElse(rp, Set()) + ((callNP, retVar))

    retEdges += rp -> set

    println(">>>> call >>>>")
    println(s"np: $callNP")
    println(s"rp: $rp")
    println(s"args: ${args.map(beautify(_)).mkString("[", ", ", "]")}")
  }

  // update return points
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
      case (np: NodePoint[_]) =>
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
  private def initNpMap: Map[NodePoint[_], AbsState] = (for {
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
    // AssignmentTargetType
    """Expression\[1,0\].AssignmentTargetType""".r,
    """IdentifierReference\[.*].AssignmentTargetType""".r,
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
    // not implemented `access`
    """PrimaryExpression\[12,0\].Evaluation""".r,
    """Identifier\[.*.StringValue""".r,
    """PropertyDefinition\[0,0\].PropName""".r,
    """StringLiteral\[0,1\].StringValue""".r,
    """PrimaryExpression\[12,0\].IsFunctionDefinition""".r,
    // not implemented transfer for `RefValue `
    """IdentifierReference\[0,0\].AssignmentTargetType""".r,
    // has parameter
    """TemplateSpans\[0,0\].SubstitutionEvaluation""".r,
    // not implemented: access Identifier "StringValue" -> maybe abstract StringValue to `str`?
    """Identifier\[0,0\].StringValue""".r,
    // not implemented: EIsInstanceOf
    """IfStatement\[0,0\].EarlyErrors""".r,
    """NewExpression\[1,0\].Evaluation""".r, // EIsInstanceOf @EvaluateNew
    // Unknown property #NamedAddr(ExecutionContext)."Generator" (@GetGeneratorKind)
    """YieldExpression\[0,0\].Evaluation""".r,
    // unknown property: #NamedAddr(EnvironmentRecord)."GetThisBinding"
    // unknown property: #NamedAddr(EnvironmentRecord)."OuterEnv"
    """PrimaryExpression\[0,0\].Evaluation""".r,
    // unknown variable: Type (@ToString)
    """LiteralPropertyName\[2,0\].Evaluation""".r,
    // unknown variable: INTRINSIC_SyntaxError
    """PropertyDefinition\[1,0\].EarlyErrors""".r,
    """IdentifierReference\[1,0\].EarlyErrors""".r,
    // not impelemented: transfer for `EParseSyntax`
    """IdentifierReference\[2,0\].EarlyErrors""".r,
    """CoverParenthesizedExpressionAndArrowParameterList\[0,0\].CoveredParenthesizedExpression""".r,
    // not implemeted: state.BasicDomain allocList
    """BindingIdentifier\[1,0\].BoundNames""".r,
    """TemplateLiteral\[0,0\].TemplateStrings""".r,
  )

  // private def targetPatterns = successPatterns
  // private def targetPatterns = failedPatterns
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

  // get types from abstract values
  private def getTypes(st: AbsState, vs: List[AbsValue]): List[List[Type]] = {
    vs.foldRight(List(List[Type]())) {
      case (v, tysList) => for {
        tys <- tysList
        ty <- getType(st, v.escaped)
      } yield ty :: tys
    }
  }
  private def getType(st: AbsState, v: AbsPure): List[Type] = {
    var tys: List[Type] = Nil
    if (!v.num.isBottom) tys ::= NumT
    if (!v.int.isBottom) tys ::= INumT
    if (!v.bigint.isBottom) tys ::= BigINumT
    if (!v.str.isBottom) tys ::= StrT
    if (!v.bool.isBottom) tys ::= BoolT
    if (!v.undef.isBottom) tys ::= UndefT
    if (!v.nullval.isBottom) tys ::= NullT
    if (!v.absent.isBottom) tys ::= AbsentT
    if (!v.ast.isBottom) tys = v.ast.toList.map(ast => AstT(ast.name)) ++ tys
    if (!v.addr.isBottom) tys = (v.addr.toList.collect {
      case NamedAddr(x) => NameT(x)
    }) ++ tys
    tys
  }
}
