package kr.ac.kaist.jiset.core

// Walker for CORE Language
trait UnitWalker {
  // all cases
  def walk(node: CoreNode): Unit = node match {
    case prog: Program => walk(prog)
    case inst: Inst => walk(inst)
    case expr: Expr => walk(expr)
    case ref: Ref => walk(ref)
    case ty: Ty => walk(ty)
    case id: Id => walk(id)
    case uop: UOp => walk(uop)
    case bop: BOp => walk(bop)
    case cop: COp => walk(cop)
    case st: State => walk(st)
    case heap: Heap => walk(heap)
    case obj: Obj => walk(obj)
    case v: Value => walk(v)
    case refV: RefValue => walk(refV)
    case ctx: Context => walk(ctx)
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

  // programs
  def walk(program: Program): Unit = walkList[Inst](program.insts, walk)

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
    case IAccess(id, bexpr, expr) =>
      walk(id); walk(bexpr); walk(expr)
    case IWithCont(id, params, body) =>
      walk(id); walkList[Id](params, walk); walk(body)
  }

  // expressions
  def walk(expr: Expr): Unit = expr match {
    case ENum(_) | EINum(_) | EStr(_) | EBool(_) | EUndef | ENull | EAbsent =>
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
    case EFunc(params, varparam, body) =>
      walkList[Id](params, walk);
      walkOpt[Id](varparam, walk);
      walk(body)
    case ECont(params, body) =>
      walkList[Id](params, walk);
      walk(body)
    case EUOp(uop, expr) =>
      walk(uop); walk(expr)
    case EBOp(bop, left, right) =>
      walk(bop); walk(left); walk(right)
    case ETypeOf(expr) =>
      walk(expr)
    case EIsInstanceOf(base, name) =>
      walk(base); walk(name)
    case EGetElems(base, name) =>
      walk(base); walk(name)
    case EGetSyntax(base) =>
      walk(base)
    case EParseSyntax(code, rule, flags) =>
      walk(code); walk(rule); walkList[Expr](flags, walk)
    case EConvert(expr, cop, list) =>
      walk(expr); walk(cop); walkList[Expr](list, walk)
    case EContains(list, elem) =>
      walk(list); walk(elem)
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

  ////////////////////////////////////////////////////////////////////////////////
  // States
  ////////////////////////////////////////////////////////////////////////////////

  // states
  def walk(st: State): Unit = {
    walk(st.context)
    walkList[Context](st.ctxStack, walk)
    walkMap[Id, Value](st.globals, walk, walk)
    walk(st.heap)
  }

  def walk(ctx: Context): Unit = {
    walk(ctx.retId)
    walk(ctx.name)
    walkList[Inst](ctx.insts, walk)
    walkMap[Id, Value](ctx.locals, walk, walk)
  }

  // heaps
  def walk(heap: Heap): Unit = {
    walkMap[Addr, Obj](heap.map, walk, walk)
  }

  // objs
  def walk(obj: Obj): Unit = obj match {
    case CoreSymbol(desc) =>
      walk(desc)
    case CoreMap(ty, props) =>
      walk(ty)
      walkMap[Value, Value](props, walk, walk)
    case CoreList(values) =>
      walkList[Value](values.toList, walk)
    case CoreNotSupported(msg) =>
      walk(msg)
  }

  // values
  def walk(v: Value): Unit = v match {
    case addr: Addr => walk(addr)
    case ast: ASTVal => walk(ast)
    case ASTMethod(func, locals) =>
      walk(func); walkMap[Id, Value](locals, walk, walk)
    case func: Func => walk(func)
    case cont: Cont => walk(cont)
    case Num(_) | INum(_) | Str(_) | Bool(_) | Undef | Null | Absent =>
  }

  // addresses
  def walk(addr: Addr): Unit = {}

  // function
  def walk(func: Func): Unit = func match {
    case Func(name, params, varparam, body) =>
      walk(name); walkList[Id](params, walk)
      walkOpt[Id](varparam, walk); walk(body)
  }

  // continuation
  def walk(cont: Cont): Unit = cont match {
    case Cont(params, body, context, ctxStack) =>
      walkList[Id](params, walk)
      walk(body)
  }

  // AST values
  def walk(ast: ASTVal): Unit = {}

  // properties
  def walk(refV: RefValue): Unit = refV match {
    case RefValueId(id) => walk(id)
    case RefValueProp(addr, value) =>
      walk(addr); walk(value)
    case RefValueString(str, name) =>
      walk(str); walk(name)
  }
}
