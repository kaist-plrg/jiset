package kr.ac.kaist.jiset.ir

// Walker for IR Language
trait Walker {
  // all cases
  def walk(node: IRNode): IRNode = node match {
    case prog: Program => walk(prog)
    case inst: Inst => walk(inst)
    case expr: Expr => walk(expr)
    case ref: Ref => walk(ref)
    case ty: Ty => walk(ty)
    case id: Id => walk(id)
    case uop: UOp => walk(uop)
    case bop: BOp => walk(bop)
  }

  // strings
  def walk(str: String): String = str

  // options
  def walkOpt[T](
    opt: Option[T],
    tWalk: T => T
  ): Option[T] = opt.map(tWalk)

  // lists
  def walkList[T](
    list: List[T],
    tWalk: T => T
  ): List[T] = list.map(tWalk)

  // maps
  def walkMap[K, V](
    map: Map[K, V],
    kWalk: K => K,
    vWalk: V => V
  ): Map[K, V] = map.map { case (k, v) => kWalk(k) -> vWalk(v) }

  ////////////////////////////////////////////////////////////////////////////////
  // Syntax
  ////////////////////////////////////////////////////////////////////////////////

  // programs
  def walk(program: Program): Program = Program(walkList[Inst](program.insts, walk))

  // instructions
  def walk(inst: Inst): Inst = {
    val newInst = inst match {
      case IExpr(expr) => IExpr(walk(expr))
      case ILet(id, expr) => ILet(walk(id), walk(expr))
      case IAssign(ref, expr) => IAssign(walk(ref), walk(expr))
      case IDelete(ref) => IDelete(walk(ref))
      case IAppend(expr, list) => IAppend(walk(expr), walk(list))
      case IPrepend(expr, list) => IPrepend(walk(expr), walk(list))
      case IReturn(expr) => IReturn(walk(expr))
      case IThrow(id) => IThrow(walk(id))
      case IIf(cond, thenInst, elseInst) => IIf(walk(cond), walk(thenInst), walk(elseInst))
      case IWhile(cond, body) => IWhile(walk(cond), walk(body))
      case ISeq(insts) => ISeq(walkList[Inst](insts, walk))
      case IAssert(expr) => IAssert(walk(expr))
      case IPrint(expr) => IPrint(walk(expr))
      case IApp(id, fexpr, args) => IApp(walk(id), walk(fexpr), walkList[Expr](args, walk))
      case IAccess(id, bexpr, expr) => IAccess(walk(id), walk(bexpr), walk(expr))
      case IWithCont(id, params, body) => IWithCont(walk(id), walkList[Id](params, walk), walk(body))
      case ISetType(expr, ty) => ISetType(walk(expr), walk(ty))
    }
    newInst.line = inst.line
    newInst
  }

  // expressions
  def walk(expr: Expr): Expr = expr match {
    case ENum(_) | EINum(_) | EBigINum(_) | EStr(_) | EBool(_) | EUndef | ENull | EAbsent => expr
    case EMap(ty, props) => EMap(
      walk(ty),
      walkList[(Expr, Expr)](props, { case (x, y) => (walk(x), walk(y)) })
    )
    case EList(exprs) => EList(walkList[Expr](exprs, walk))
    case ESymbol(desc) => ESymbol(walk(desc))
    case EPop(list, idx) => EPop(walk(list), walk(idx))
    case ERef(ref) => ERef(walk(ref))
    case ECont(params, body) => ECont(walkList[Id](params, walk), walk(body))
    case EUOp(uop, expr) => EUOp(walk(uop), walk(expr))
    case EBOp(bop, left, right) => EBOp(walk(bop), walk(left), walk(right))
    case ETypeOf(expr) => ETypeOf(walk(expr))
    case EIsCompletion(expr) => EIsCompletion(walk(expr))
    case EIsInstanceOf(base, name) => EIsInstanceOf(walk(base), walk(name))
    case EGetElems(base, name) => EGetElems(walk(base), walk(name))
    case EGetSyntax(base) => EGetSyntax(walk(base))
    case EParseSyntax(code, rule, flags) => EParseSyntax(walk(code), walk(rule), walk(flags))
    case EConvert(expr, cop, list) => EConvert(walk(expr), walk(cop), walkList[Expr](list, walk))
    case EContains(list, elem) => EContains(walk(list), walk(elem))
    case ECopy(obj) => ECopy(walk(obj))
    case EKeys(obj) => EKeys(walk(obj))
    case ENotSupported(msg) => ENotSupported(walk(msg))
  }

  // references
  def walk(ref: Ref): Ref = ref match {
    case RefId(id) => RefId(walk(id))
    case RefProp(ref, id) => RefProp(walk(ref), walk(id))
  }

  // types
  def walk(ty: Ty): Ty = ty

  // identifiers
  def walk(id: Id): Id = id

  // unary operators
  def walk(uop: UOp): UOp = uop

  // binary operators
  def walk(bop: BOp): BOp = bop

  def walk(cop: COp): COp = cop
}
