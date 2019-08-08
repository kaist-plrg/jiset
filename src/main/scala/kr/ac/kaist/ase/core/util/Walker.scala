package kr.ac.kaist.ase.core

// Walker for CORE Language
trait Walker {
  // all cases
  def walk(node: CoreNode): CoreNode = node match {
    case prog: Program => walk(prog)
    case inst: Inst => walk(inst)
    case expr: Expr => walk(expr)
    case ref: Ref => walk(ref)
    case ty: Ty => walk(ty)
    case id: Id => walk(id)
    case uop: UOp => walk(uop)
    case bop: BOp => walk(bop)
    case st: State => walk(st)
    case heap: Heap => walk(heap)
    case obj: Obj => walk(obj)
    case v: Value => walk(v)
    case refV: RefValue => walk(refV)
    case ctx: Context => walk(ctx)
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
  def walk(inst: Inst): Inst = inst match {
    case IExpr(expr) => IExpr(walk(expr))
    case ILet(id, expr) => ILet(walk(id), walk(expr))
    case IAssign(ref, expr) => IAssign(walk(ref), walk(expr))
    case IDelete(ref) => IDelete(walk(ref))
    case IAppend(expr, list) => IAppend(walk(expr), walk(list))
    case IPrepend(expr, list) => IPrepend(walk(expr), walk(list))
    case IReturn(expr) => IReturn(walk(expr))
    case IIf(cond, thenInst, elseInst) => IIf(walk(cond), walk(thenInst), walk(elseInst))
    case IWhile(cond, body) => IWhile(walk(cond), walk(body))
    case ISeq(insts) => ISeq(walkList[Inst](insts, walk))
    case IAssert(expr) => IAssert(walk(expr))
    case IPrint(expr) => IPrint(walk(expr))
    case IApp(id, fexpr, args) => IApp(walk(id), walk(fexpr), walkList[Expr](args, walk))
    case IAccess(id, bexpr, expr) => IAccess(walk(id), walk(bexpr), walk(expr))
    case IWithCont(id, params, body) => IWithCont(walk(id), walkList[Id](params, walk), walk(body))
  }

  // expressions
  def walk(expr: Expr): Expr = expr match {
    case ENum(_) | EINum(_) | EStr(_) | EBool(_) | EUndef | ENull | EAbsent => expr
    case EMap(ty, props) => EMap(
      walk(ty),
      walkList[(Expr, Expr)](props, { case (x, y) => (walk(x), walk(y)) })
    )
    case EList(exprs) => EList(walkList[Expr](exprs, walk))
    case ESymbol(desc) => ESymbol(walk(desc))
    case EPop(list, idx) => EPop(walk(list), walk(idx))
    case ERef(ref) => ERef(walk(ref))
    case EFunc(params, varparam, body) => EFunc(walkList[Id](params, walk), walkOpt[Id](varparam, walk), walk(body))
    case ECont(params, body) => ECont(walkList[Id](params, walk), walk(body))
    case EUOp(uop, expr) => EUOp(walk(uop), walk(expr))
    case EBOp(bop, left, right) => EBOp(walk(bop), walk(left), walk(right))
    case ETypeOf(expr) => ETypeOf(walk(expr))
    case EIsInstanceOf(base, name) => EIsInstanceOf(walk(base), walk(name))
    case EGetElems(base, name) => EGetElems(walk(base), walk(name))
    case EGetSyntax(base) => EGetSyntax(walk(base))
    case EParseSyntax(code, rule, flags) => EParseSyntax(walk(code), walk(rule), walkList[Expr](flags, walk))
    case EParseString(code, pop) => EParseString(walk(code), walk(pop))
    case EConvert(expr, cop, list) => EConvert(walk(expr), walk(cop), walkList[Expr](list, walk))
    case EContains(list, elem) => EContains(walk(list), walk(elem))
    case ECopy(obj) => ECopy(walk(obj))
    case EKeys(obj) => EKeys(walk(obj))
    case ENotYetImpl(msg) => ENotYetImpl(walk(msg))
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

  def walk(pop: POp): POp = pop

  def walk(cop: COp): COp = cop
  ////////////////////////////////////////////////////////////////////////////////
  // States
  ////////////////////////////////////////////////////////////////////////////////

  // states
  def walk(st: State): State = State(
    walk(st.context),
    walkList[Context](st.ctxStack, walk),
    walkMap[Id, Value](st.globals, walk, walk),
    walk(st.heap)
  )

  def walk(ctx: Context): Context = Context(
    walk(ctx.retId),
    walk(ctx.name),
    walkList[Inst](ctx.insts, walk),
    walkMap[Id, Value](ctx.locals, walk, walk)
  )

  // heaps
  def walk(heap: Heap): Heap = Heap(
    walkMap[Addr, Obj](heap.map, walk, walk),
    heap.size
  )

  // objects
  def walk(obj: Obj): Obj = obj match {
    case CoreSymbol(desc) => CoreSymbol(walk(desc))
    case CoreMap(ty, props) => CoreMap(
      walk(ty),
      walkMap[Value, Value](props, walk, walk)
    )
    case CoreList(values) => CoreList(
      walkList[Value](values.toList, walk).toVector
    )
    case CoreNotSupported(msg) => CoreNotSupported(walk(msg))
  }

  // values
  def walk(value: Value): Value = value match {
    case addr: Addr => walk(addr)
    case ast: ASTVal => walk(ast)
    case ASTMethod(func, locals) => ASTMethod(walk(func), walkMap[Id, Value](locals, walk, walk))
    case func: Func => walk(func)
    case cont: Cont => walk(cont)
    case Num(_) | INum(_) | Str(_) | Bool(_) | Undef | Null | Absent => value
  }

  // addresses
  def walk(addr: Addr): Addr = addr

  // function
  def walk(func: Func): Func = func match {
    case Func(name, params, varparam, body) =>
      Func(walk(name), walkList[Id](params, walk), walkOpt[Id](varparam, walk), walk(body))
  }

  def walk(cont: Cont): Cont = cont match {
    case Cont(params, body, context, ctxStack) =>
      Cont(walkList[Id](params, walk), walk(body), walk(context), walkList[Context](ctxStack, walk))
  }

  // AST values
  def walk(ast: ASTVal): ASTVal = ast

  // reference values
  def walk(refV: RefValue): RefValue = refV match {
    case RefValueId(id) => RefValueId(walk(id))
    case RefValueProp(addr, value) => RefValueProp(walk(addr), walk(value))
    case RefValueString(str, name) => RefValueString(walk(str), walk(name))
  }
}
