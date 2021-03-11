package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.analyzer.domain.Beautifier._
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.spec.algorithm.{ Algo, Head, SyntaxDirectedHead, Param }
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util.Appender
import scala.Console._
import scala.util.matching.Regex

class AbsSemantics(
  val cfg: CFG,
  val target: Option[String] = None
) {
  // ECMAScript
  lazy val spec: ECMAScript = cfg.spec

  // internal map from control points to abstract states
  private var npMap: Map[NodePoint[_], AbsState] = initNpMap
  private var rpMap: Map[ReturnPoint, (AbsHeap, AbsValue)] = Map()

  // internal map for return edges
  private var retEdges: Map[ReturnPoint, Set[(NodePoint[Call], String)]] = Map()

  // worklist
  lazy val worklist: Worklist[ControlPoint] = new StackWorklist(npMap.keySet)

  // global variables
  lazy val (globalEnv, globalHeap) = model.global

  // type map
  lazy val typeMap = model.typeMap

  // manual model
  lazy val model = new Model(cfg)

  // statistics
  lazy val stat = new Stat(this)

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

  def size: Int = npMap.size + rpMap.size

  // update internal map
  def +=[T <: Node](pair: (NodePoint[T], AbsState)): Boolean = {
    val (np, newSt) = pair
    val oldSt = this(np)
    if (!(newSt ⊑ oldSt)) {
      npMap += np -> (oldSt ⊔ newSt)
      worklist += stat.inc(np)
      true
    }
    false
  }

  // handle parameters
  def getEnv(
    call: Call,
    params: List[Param],
    args: List[AbsValue]
  ): List[(String, AbsValue)] = (params, args) match {
    case (param :: pl, arg :: al) =>
      (param.name -> arg) :: getEnv(call, pl, al)
    case (Param(name, Param.Kind.Optional) :: tl, Nil) =>
      (name -> ABSENT) :: getEnv(call, tl, Nil)
    case (Param(name, Param.Kind.Normal) :: tl, Nil) =>
      alarm(s"arity mismatch @ $call")
      (name -> ABSENT) :: getEnv(call, tl, Nil)
    case (Nil, Nil) => Nil
    case _ => ??? // TODO consider variadic
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
    // TODO consider variadic
    val params = func.algo.params
    if (params.exists(_.kind == Param.Kind.Variadic)) ???
    val pairs = getEnv(call, params, args)
    val np = NodePoint(func.entry, view)
    val newSt = pairs.foldLeft(st.copy(env = AbsEnv.Empty)) {
      case (st, (x, v)) => st + (x -> v)
    }
    this += np -> newSt

    val rp = ReturnPoint(func, view)
    val callNP = NodePoint(call, callView)
    val set = retEdges.getOrElse(rp, Set()) + ((callNP, retVar))
    retEdges += rp -> set

    val (_, retV) = this(rp)
    if (!retV.isBottom) worklist += rp
  }

  // update return points
  def doReturn(pair: (ReturnPoint, (AbsHeap, AbsValue))): Unit = {
    val (rp, (newH, newV)) = pair
    val (oldH, oldV) = this(rp)
    if (!(newH ⊑ oldH && newV ⊑ oldV)) {
      rpMap += rp -> (oldH ⊔ newH, oldV ⊔ newV)
      worklist += stat.inc(rp)
    }
  }

  // get function of given control points
  def funcOf(cp: ControlPoint): Function = cp match {
    case NodePoint(node, _) => cfg.funcOf(node)
    case ReturnPoint(func, _) => func
  }

  // conversion to string
  override def toString: String = getString("")
  def getInfo: String = {
    val app = new Appender
    val numRp = rpMap.size
    val numFunc = rpMap.keySet.map(_.func).toSet.size
    app >> numFunc >> " out of " >> spec.algos.length >> " functions analyzed with "
    app >> numRp >> " return points" >> LINE_SEP
    app >> "# of iterations: " >> stat.iter
    app.toString
  }

  // get string for result of control points
  def getString(color: String): String = rpMap.keySet.toList.map(rp => {
    val ReturnPoint(func, view) = rp
    val entryCP = NodePoint(func.entry, view)
    val from = beautify(this(entryCP))
    val (h, v) = this(rp)
    val to = beautify(v) + (if (h.isBottom) "" else s" @ ${beautify(h)}")
    setColor(color)(s"${func.name}:$view:") + s" $from ---> $to"
  }).sorted.mkString(LINE_SEP)
  def getString(cp: ControlPoint): String = getString(cp, "", true)
  def getString(cp: ControlPoint, color: String, detail: Boolean): String = {
    val func = funcOf(cp).name
    val k = setColor(color)(s"$func:$cp")
    if (detail) {
      val v = cp match {
        case (np: NodePoint[_]) =>
          val st = this(np)
          beautify(st)
        case (rp: ReturnPoint) =>
          val (h, v) = this(rp)
          beautify(v) + (
            if (h.isBottom) ""
            else s" @ ${beautify(h)}"
          )
      }
      s"$k -> $v"
    } else k
  }

  // get arguments
  def getArgs(head: SyntaxDirectedHead): List[AbsValue] = head.types.map {
    case (name, astName) =>
      val v = AbsValue(ASTVal(astName))
      if (head.optional contains name) v ⊔ AbsAbsent.Top
      else v
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
    """.*.AssignmentTargetType""".r,
    // String Value : success except algorithms with not-yet-compiled instructions
    """BindingIdentifier[1,0].StringValue""".r,
    """BindingIdentifier[2,0].StringValue""".r,
    """CodePointsToString""".r,
    """IdentifierName[1,0].StringValue""".r,
    """IdentifierReference[1,0].StringValue""".r,
    """IdentifierReference[2,0].StringValue""".r,
    """Identifier[0,0].StringValue""".r,
    """LabelIdentifier[1,0].StringValue""".r,
    """LabelIdentifier[2,0].StringValue""".r,
    """StringLiteral[0,1].StringValue""".r,
    """StringLiteral[0,1].StringValue""".r,
    """StringLiteral[1,1].StringValue""".r,
    """StringLiteral[1,1].StringValue""".r,
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
    // .HasName
    """.*.HasName""".r,
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

  private def isTarget(head: SyntaxDirectedHead, inst: Inst): Boolean = (
    head.withParams.isEmpty && (target match {
      case Some(pattern) => pattern.r.matches(head.printName)
      case None => (
        successPatterns.exists(_.matches(head.printName)) ||
        isSimple(inst)
      )
    })
  )

  private def isSimple(inst: Inst): Boolean = inst match {
    case IReturn(EBool(_)) => true
    case _ => false
  }

  // initial abstract state for syntax-directed algorithms
  private def getTypes(algo: Algo): List[(List[Type], AbsState)] = algo.head match {
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
    if (!v.addr.isBottom) tys ++= v.addr.toSet.flatMap(addr => {
      import AbsObj._
      st.lookup(this, addr) match {
        case MapElem(Some(ty), _) => Some(NameT(ty))
        case ListElem(_) => Some(ListT)
        case SymbolElem(_) => Some(SymbolT)
        case Bot =>
          alarm(s"no objects for ${beautify(addr)} @ AbsSemantics.getType")
          None
        case _ =>
          ???
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
