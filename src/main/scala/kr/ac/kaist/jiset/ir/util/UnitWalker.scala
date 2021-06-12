package kr.ac.kaist.jiset.ir

// Walker for IR Language
trait UnitWalker {
  // all cases
  def walk(node: IRNode): Unit = node match {
    case inst: Inst => walk(inst)
    case expr: Expr => walk(expr)
    case ref: Ref => walk(ref)
    case ty: Ty => walk(ty)
    case id: Id => walk(id)
    case uop: UOp => walk(uop)
    case bop: BOp => walk(bop)
    case cop: COp => walk(cop)
  }

  // strings
  def walk(str: String): Unit = {}

  // options
  def walkOpt[T](
    opt: Option[T],
    tWalk: T => Unit
  ): Unit = opt.foreach(tWalk)

  // lists
  def walkList[T](
    list: List[T],
    tWalk: T => Unit
  ): Unit = list.foreach(tWalk)

  // maps
  def walkMap[K, V](
    map: Map[K, V],
    kWalk: K => Unit,
    vWalk: V => Unit
  ): Unit = map.foreach { case (k, v) => kWalk(k); vWalk(v) }

  ////////////////////////////////////////////////////////////////////////////////
  // Syntax
  ////////////////////////////////////////////////////////////////////////////////
  // instructions
  def walk(inst: Inst): Unit = inst match {
    case IExpr(expr) =>
      walk(expr)
    case ILet(id, expr) =>
      walk(id); walk(expr)
    case IAssign(ref, expr) =>
      walk(ref); walk(expr)
    case IDelete(ref) =>
      walk(ref)
    case IAppend(expr, list) =>
      walk(expr); walk(list)
    case IPrepend(expr, list) =>
      walk(expr); walk(list)
    case IReturn(expr) =>
      walk(expr)
    case IThrow(id) =>
      walk(id)
    case IIf(cond, thenInst, elseInst) =>
      walk(cond); walk(thenInst); walk(elseInst)
    case IWhile(cond, body) =>
      walk(cond); walk(body)
    case ISeq(insts) =>
      walkList[Inst](insts, walk)
    case IAssert(expr) =>
      walk(expr)
    case IPrint(expr) =>
      walk(expr)
    case IApp(id, fexpr, args) =>
      walk(id); walk(fexpr); walkList[Expr](args, walk)
    case IAccess(id, bexpr, expr, args) =>
      walk(id); walk(bexpr); walk(expr); walkList[Expr](args, walk)
    case IWithCont(id, params, body) =>
      walk(id); walkList[Id](params, walk); walk(body)
  }

  // expressions
  def walk(expr: Expr): Unit = expr match {
    case ENum(_) | EINum(_) | EBigINum(_) | EStr(_) | EBool(_) | EUndef | ENull | EAbsent =>
    case EMap(ty, props) =>
      walk(ty); walkList[(Expr, Expr)](props, { case (x, y) => (walk(x), walk(y)) })
    case EList(exprs) =>
      walkList[Expr](exprs, walk)
    case ESymbol(desc) =>
      walk(desc)
    case EPop(list, idx) =>
      walk(list); walk(idx)
    case ERef(ref) =>
      walk(ref)
    case ECont(params, body) =>
      walkList[Id](params, walk);
      walk(body)
    case EUOp(uop, expr) =>
      walk(uop); walk(expr)
    case EBOp(bop, left, right) =>
      walk(bop); walk(left); walk(right)
    case ETypeOf(expr) =>
      walk(expr)
    case EIsCompletion(expr) =>
      walk(expr)
    case EIsInstanceOf(base, name) =>
      walk(base); walk(name)
    case EGetElems(base, name) =>
      walk(base); walk(name)
    case EGetSyntax(base) =>
      walk(base)
    case EParseSyntax(code, rule, flags) =>
      walk(code); walk(rule); walk(flags)
    case EConvert(expr, cop, list) =>
      walk(expr); walk(cop); walkList[Expr](list, walk)
    case EContains(list, elem) =>
      walk(list); walk(elem)
    case EReturnIfAbrupt(expr, check) =>
      walk(expr)
    case ECopy(obj) =>
      walk(obj)
    case EKeys(obj) =>
      walk(obj)
    case ENotSupported(msg) =>
      walk(msg)
  }

  // references
  def walk(ref: Ref): Unit = ref match {
    case RefId(id) => walk(id)
    case RefProp(ref, expr) =>
      walk(ref); walk(expr)
  }

  // types
  def walk(ty: Ty): Unit = {}

  // identifiers
  def walk(id: Id): Unit = {}

  // unary operators
  def walk(uop: UOp): Unit = {}

  // binary operators
  def walk(bop: BOp): Unit = {}

  // convert operators
  def walk(cop: COp): Unit = {}
}
