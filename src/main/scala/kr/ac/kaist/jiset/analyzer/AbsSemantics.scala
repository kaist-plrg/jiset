package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.analyzer.domain.Beautifier._
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.spec.algorithm.{ Algo, Head, SyntaxDirectedHead }
import kr.ac.kaist.jiset.util.Useful._
import scala.Console._
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
  lazy val (globalEnv, globalHeap): (Map[String, AbsValue], Map[Addr, AbsObj]) = {
    val (env, heaps) = cfg.getGlobal
    val globalEnv = manualEnv ++ (for ((x, v) <- env) yield x -> AbsValue(v))
    var globalHeap: Map[Addr, AbsObj] = (for ((x, (p, m)) <- manualHeaps) yield {
      val map: Map[String, AbsObj.MapD.AbsVOpt] = m.map {
        case (k, v) => k -> AbsObj.MapD.AbsVOpt(v, AbsAbsent.Bot)
      }
      NamedAddr(x) -> AbsObj.MapElem(p, AbsObj.MapD(map, AbsObj.MapD.AbsVOpt(None)))
    }).toMap
    globalHeap ++= (for ((a, o) <- heaps) yield a -> AbsObj(o))
    (globalEnv, globalHeap)
  }

  // type map
  lazy val typeMap: Map[String, TyInfo] =
    typeInfos.map(info => info.name -> info).toMap

  // TODO more manual modelings
  private def typeInfos: List[TyInfo] = List(
    TyInfo(
      name = "ExecutionContext",
      "LexicalEnvironment" -> AbsValue(Ty("EnvironmentRecord")),
    ),
    TyInfo(
      name = "EnvironmentRecord",
      "HasThisBinding" -> getClos(""".*\.HasThisBinding""".r),
      "GetThisBinding" -> getClos(""".*\.GetThisBinding""".r),
      "ThisBindingStatus" -> getConsts("lexical", "initialized", "uninitialized"),
      "OuterEnv" -> AbsValue(Ty("EnvironmentRecord")) ⊔ AbsValue(Null),
      "GlobalThisValue" -> AbsValue(NamedAddr("Global")),
      "ThisValue" -> ESValue,
    ),
    TyInfo(
      name = "DeclarativeEnvironmentRecord",
      "CreateImmutableBinding" -> getClos(""".*\.CreateImmutableBinding""".r),
    ),
    TyInfo(
      name = "Object",
    ),
    TyInfo(
      name = "OrdinaryObject",
      parent = "Object"
    ),
  )
  // TODO more manual modelings
  private def manualEnv: Map[String, AbsValue] = Map(
    "GLOBAL_context" -> AbsValue(Ty("ExecutionContext")),
  )
  // TODO more manual modelings
  private def manualHeaps: Map[String, (Option[String], Map[String, AbsValue])] = Map(
    "Global" -> (Some("OrdinaryObject"), Map()),
  )
  private val ESValue: AbsValue = {
    val prim = AbsPrim.Top.copy(absent = AbsAbsent.Bot)
    AbsPure(ty = AbsTy("Object"), prim = prim)
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
    val pairs = func.algo.params.map(_.name) zip args // TODO pruning args using types
    val np = NodePoint(func.entry, view)
    val newSt = pairs.foldLeft(st.copy(env = AbsEnv.Empty)) {
      case (st, (x, v)) => st + (x -> v)
    }
    this += np -> newSt

    val rp = ReturnPoint(func, view)
    val callNP = NodePoint(call, callView)
    val set = retEdges.getOrElse(rp, Set()) + ((callNP, retVar))

    retEdges += rp -> set
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
  override def toString: String = {
    val res = rpMap.keySet.toList.map(getString).sorted.mkString(LINE_SEP)
    val numRp = rpMap.size
    val numFunc = rpMap.keySet.map(_.func).toSet.size
    res + LINE_SEP + s"${numFunc} functions analyzed with ${numRp} return points"
  }

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

  // get arguments
  def getArgs(head: SyntaxDirectedHead): List[AbsValue] = head.types.map {
    case (name, astName) =>
      val v = AbsValue(ASTVal(astName))
      if (head.optional contains name) v ⊔ AbsAbsent.Top
      else v
  }

  // lookup types
  def lookup(ty: String, prop: AbsStr): AbsValue = typeMap.get(ty) match {
    case Some(info) =>
      val fields = info.fields
      prop.gamma match {
        case Infinite => AbsValue.Top
        case Finite(ps) =>
          // TODO follow ancestors
          // TODO alarm unknown property
          ps.toList.map(p => fields(p.str)).foldLeft(AbsValue.Bot)(_ ⊔ _)
      }
    case None =>
      alarm(s"unknown type: $ty")
      AbsValue.Bot
  }

  //////////////////////////////////////////////////////////////////////////////
  // Private Helper Functions
  //////////////////////////////////////////////////////////////////////////////
  // initialization of node points with abstract states
  private def initNpMap: Map[NodePoint[_], AbsState] = (for {
    func <- cfg.funcs.toList
    (types, st) <- getTypes(func.algo)
    view = View(types)
    cp = NodePoint(func.entry, view)
  } yield cp -> st).toMap

  // target algorithms
  private def successPatterns = List(
    // Success
    """Literal\[.*""".r,
    // """PrimaryExpression.*IsIdentifierRef""".r,
    // IsIdentifierRef
    """.*.IsIdentifierRef""".r,
    // .IsFunctionDefinition
    """.*.IsFunctionDefinition""".r,
    // AssignmentTargetType
    """Expression\[1,0\].AssignmentTargetType""".r,
    """IdentifierReference\[.*].AssignmentTargetType""".r,
    // String Value
    """IdentifierReference\[1,0\].StringValue""".r,
    """BindingIdentifier\[.*.StringValue""".r,
    """LabelIdentifier\[.*.StringValue""".r,
    """StringLiteral\[.*.StringValue""".r,
    """IdentifierReference\[.*.StringValue""".r,
    """Identifier\[.*.StringValue""".r,
    """StringLiteral\[0,1\].StringValue""".r,
    // PropName
    """PropertyDefinition\[0,0\].PropName""".r,
    """LiteralPropertyName\[1,0\].PropName""".r,
    // EarlyErrors
    """PropertyDefinition\[1,0\].EarlyErrors""".r,
    """IdentifierReference\[1,0\].EarlyErrors""".r,
    // Evaluation
    """LiteralPropertyName\[1,0\].Evaluation""".r,
    """PrimaryExpression\[0,0\].Evaluation""".r,
    // CoveredParenthesizedExpression
    """CoverParenthesizedExpressionAndArrowParameterList\[0,0\].CoveredParenthesizedExpression""".r,
    // BoundNames
    """BindingIdentifier\[1,0\].BoundNames""".r,
    // DeclarationPart - only 6 cases, all pass
    """.*.DeclarationPart""".r,
    // ImportEntries
    """Module\[0,0\].ImportEntries""".r,
    """ModuleItem\[.*.ImportEntries""".r,
    """ImportDeclaration\[1,0\].ImportEntries""".r,
    // IsDestructuring
    """.*.IsDestructuring""".r,
  )

  private def failedPatterns = List(
    // has parameter
    """TemplateSpans\[0,0\].SubstitutionEvaluation""".r,
    """TemplateLiteral\[0,0\].TemplateStrings""".r,
    """BreakableStatement\[0,0\].ContainsUndefinedContinueTarget""".r,
    // not implemented: EIsInstanceOf
    """IfStatement\[0,0\].EarlyErrors""".r,
    // Unknown property #Ty(ExecutionContext)."Generator" (@GetGeneratorKind)
    """YieldExpression\[0,0\].Evaluation""".r,
    // unknown variable: Type (@ToString)
    """LiteralPropertyName\[2,0\].Evaluation""".r,
    // not impelemented: transfer for `EParseSyntax`
    """IdentifierReference\[2,0\].EarlyErrors""".r,
    // EIsInstanceOf @EvaluateNew
    """NewExpression\[1,0\].Evaluation""".r,
    //  not detected in target
    """ImportDeclaration\[0,0\].ImportEntries""".r,
    // instead, targetted in this query
    """ModuleItemList\[1,0\].ImportEntries""".r,
    // not implemented access
    """AsyncFunctionExpression\[0,0\].Evaluation""".r,
    // not implemented access: "Contains"
    """ArrowFunction\[0,0\].EarlyErrors""".r,
    // unknown property: "CreateImmutableBinding"
    """AsyncFunctionExpression\[1,0\].Evaluation""".r,
  )

  private def targetPatterns = List(
    // """NewExpression\[1,0\].Evaluation""".r,
    """AsyncFunctionExpression\[1,0\].Evaluation""".r,
  )

  private def isTarget(head: SyntaxDirectedHead, inst: Inst): Boolean = (
    head.withParams.isEmpty &&
    targetPatterns.exists(_.matches(head.printName))
  )

  private def isSuccess(head: SyntaxDirectedHead, inst: Inst): Boolean = (
    head.withParams.isEmpty && (
      successPatterns.exists(_.matches(head.printName)) ||
      isSimple(inst)
    )
  )

  private def isSimple(inst: Inst): Boolean = inst match {
    case IReturn(EBool(_)) => true
    case _ => false
  }

  // initial abstract state for syntax-directed algorithms
  private def getTypes(algo: Algo): List[(List[Type], AbsState)] = algo.head match {
    // case (head: SyntaxDirectedHead) if isSuccess(head, algo.rawBody) =>
    case (head: SyntaxDirectedHead) if isTarget(head, algo.rawBody) =>
      head.optional.subsets.map(opt => {
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
  private def getType(st: AbsState, v: AbsPure): Set[Type] = {
    var tys = Set[Type]()
    if (!v.ty.isBottom) tys ++= v.ty.toSet.map(t => NameT(t.name))
    if (!v.addr.isBottom) tys ++= v.addr.toSet.map(addr => {
      import AbsObj._
      st(this, addr) match {
        case MapElem(Some(ty), _) => NameT(ty)
        case _ => ???
      }
    })
    if (!v.const.isBottom) tys ++= v.const.toSet.map(c => ConstT(c.const))
    if (!v.clo.isBottom) ???
    if (!v.cont.isBottom) ???
    if (!v.ast.isBottom) tys ++= v.ast.toSet.map(ast => AstT(ast.name))
    if (!v.num.isBottom) tys += NumT
    if (!v.int.isBottom) tys += INumT
    if (!v.bigint.isBottom) tys += BigINumT
    if (!v.str.isBottom) tys += StrT
    if (!v.bool.isBottom) tys += BoolT
    if (!v.undef.isBottom) tys += UndefT
    if (!v.nullval.isBottom) tys += NullT
    if (!v.absent.isBottom) tys += AbsentT
    tys
  }
}
