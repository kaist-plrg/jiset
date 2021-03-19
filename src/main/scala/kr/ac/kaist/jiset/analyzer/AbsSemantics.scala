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
  val target: Option[String] = None,
  val useGC: Boolean = true
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
  def apply(uid: Int): List[NodePoint[_]] = npMap.keys.filter {
    case NodePoint(node, _) => node.uid == uid
  }.toList
  def apply(fname: String): List[ReturnPoint] =
    rpMap.keys.filter(_.func.name == fname).toList
  def apply(np: NodePoint[_]): AbsState = npMap.getOrElse(np, AbsState.Bot)
  def apply(rp: ReturnPoint): (AbsHeap, AbsValue) =
    rpMap.getOrElse(rp, (AbsHeap.Bot, AbsValue.Bot))

  // get return edges
  def getAllRetEdges = retEdges
  def getRetEdges(rp: ReturnPoint): Set[(NodePoint[Call], String)] =
    retEdges.getOrElse(rp, Set())

  // get all control points
  def getControlPoints: Set[ControlPoint] =
    npMap.keySet ++ rpMap.keySet

  // get node point map
  def getNpMap = npMap

  // get return point map
  def getRpMap = rpMap

  // get type ancestors
  def getTypes(name: String): Set[String] = (for {
    info <- typeMap.get(name)
    parent <- info.parent
  } yield getTypes(parent)).getOrElse(Set()) + name

  // get size
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
      val paramsStr = params.mkString(", ")
      alarm(s"arity mismatch (remaining normal parameters): $paramsStr")
      (name -> ABSENT) :: getEnv(call, tl, Nil)
    case (Nil, Nil) => Nil
    case (Nil, _) =>
      val argsStr = args.map(beautify(_)).mkString(", ")
      alarm(s"arity mismatch (remaining arguments): $argsStr")
      Nil
    case _ =>
      println(s"$call $params $args")
      ??? // TODO consider variadic
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
    pairs <- getTypes(st, args)
    (ts, vs) = pairs.unzip
    view = View(ts)
    pair <- f
  } {
    val AbsClo.Pair(fid, _) = pair // TODO handle envrionments
    val func = cfg.fidMap(fid)
    // TODO consider variadic
    val params = func.algo.params
    if (params.exists(_.kind == Param.Kind.Variadic)) ???
    val pairs = getEnv(call, params, vs)
    val np = NodePoint(func.entry, view)
    val (newSt, root) = pairs.foldLeft((AbsState.Empty, AbsValue.Bot)) {
      case ((st, root), (x, v)) => (st + (x -> v), root ⊔ v)
    }

    // garbage collection
    val newH = if (useGC) GC(st.heap, root) else st.heap
    this += np -> newSt.copy(heap = newH)

    val rp = ReturnPoint(func, view)
    val callNP = NodePoint(call, callView)
    val set = retEdges.getOrElse(rp, Set()) + ((callNP, retVar))
    retEdges += rp -> set

    val (_, retV) = this(rp)
    if (!retV.isBottom) worklist += rp
  }

  // update return points
  def doReturn(pair: (ReturnPoint, (AbsHeap, AbsValue))): Unit = {
    val (rp, (tempH, newV)) = pair
    val newH = if (useGC) {
      val entryH = this(NodePoint(rp.func.entry, rp.view)).heap
      val root = entryH.keySet.foldLeft(newV)(_ ⊔ AbsValue.alpha(_))
      GC(tempH, root)
    } else tempH
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
    val (numFunc, numAlgo, numRp) = numOfFuncAlgoRp
    app >> numFunc >> " out of " >> numAlgo >> " functions analyzed with "
    app >> numRp >> " return points" >> LINE_SEP
    app >> "# of iterations: " >> stat.iter
    app.toString
  }
  def numOfFuncAlgoRp: (Int, Int, Int) = (
    rpMap.keySet.map(_.func).toSet.size,
    spec.algos.length, rpMap.size
  )

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
    // algorithms
    """.*.IsIdentifierRef""".r,
    """.*.IsFunctionDefinition""".r,
    """.*.AssignmentTargetType""".r,
    """.*.StringValue""".r,
    """.*.PropName""".r,
    """.*.BoundNames""".r,
    """.*.DeclarationPart""".r,
    """.*.IsDestructuring""".r,
    """.*.HasName""".r,
    """.*.ExportEntries""".r,
    """.*.ImportEntries""".r,
    """.*.ExportedNames""".r,
    """.*.ExportedBindings""".r,
    """.*.ExportEntriesForModule""".r,
    """.*.IsConstantDeclaration""".r,
    """.*.ModuleRequests""".r,
    """.*.IsSimpleParameterList""".r,
    """.*.CoveredFormalsList""".r,
    // PrimaryExpression.Evaluation
    """PrimaryExpression\[0,0\].Evaluation""".r,
    """IdentifierReference\[.*.Evaluation""".r,
    """Literal\[.*.Evaluation""".r,
    // syntax
    """Literal\[.*""".r,
    """LiteralPropertyName\[.*""".r,
    """PropertyName\[.*""".r,
    """ImportMeta\[.*""".r,
    // EarlyErrors
    """PropertyDefinition\[1,0\].EarlyErrors""".r,
    """IdentifierReference\[1,0\].EarlyErrors""".r,
    // CoveredParenthesizedExpression
    """CoverParenthesizedExpressionAndArrowParameterList\[0,0\].CoveredParenthesizedExpression""".r,
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
  private def getTypes(
    st: AbsState,
    vs: List[AbsValue]
  ): List[List[(Type, AbsValue)]] = {
    vs.foldRight(List(List[(Type, AbsValue)]())) {
      case (v, tysList) => for {
        tys <- tysList
        ty <- getType(st, v)
      } yield ty :: tys
    }
  }
  private def getType(st: AbsState, v: AbsValue): Map[Type, AbsValue] = {
    val AbsValue(pure, comp) = v
    var tys: Map[Type, AbsValue] = Map()
    for ((t, v) <- getPureType(st, pure)) tys += t -> v
    val (normalV, _) = comp(CompNormal)
    if (!normalV.isBottom) for ((t, v) <- getPureType(st, normalV)) {
      tys += NormalT(t) -> comp.normal
    }
    val abrupt = comp.abrupt
    if (!abrupt.isBottom) tys += AbruptT -> abrupt
    tys
  }
  private def getPureType(st: AbsState, v: AbsPure): Map[PureType, AbsValue] = {
    import AbsObj._
    var tys = Map[PureType, AbsValue]()
    def add(ty: PureType, value: AbsValue): Unit =
      tys += ty -> (tys.getOrElse(ty, AbsValue.Bot) ⊔ value)
    if (!v.ty.isBottom) for (Ty(name) <- v.ty.toSet) {
      add(NameT(name), AbsTy(name))
    }
    if (!v.loc.isBottom) for (loc <- v.loc.toSet) st.lookupLoc(this, loc) match {
      case MapElem(Some(ty), _) => add(NameT(ty), AbsValue(loc))
      case ListElem(_) => add(ListT, AbsValue(loc))
      case SymbolElem(_) => add(SymbolT, AbsValue(loc))
      case Bot =>
        alarm(s"no objects for ${beautify(loc)} @ AbsSemantics.getPureType")
      case _ =>
        ???
    }
    if (!v.const.isBottom) for (Const(c) <- v.const.toSet) {
      add(ConstT(c), AbsConst(c))
    }
    if (!v.clo.isBottom) for (clo <- v.clo) {
      add(CloT(clo.fid), AbsClo(clo))
    }
    if (!v.cont.isBottom) ???
    if (!v.ast.isBottom) for (ast <- v.ast.toSet) {
      add(AstT(ast.name), AbsValue(ast))
    }
    if (!v.num.isBottom) add(NumT, v.num)
    if (!v.bigint.isBottom) add(BigINumT, v.bigint)
    if (!v.str.isBottom) add(StrT, v.str)
    if (!v.bool.isBottom) add(BoolT, v.bool)
    if (!v.undef.isBottom) add(UndefT, v.undef)
    if (!v.nullval.isBottom) add(NullT, v.nullval)
    if (!v.absent.isBottom) add(AbsentT, v.absent)
    tys
  }
}
