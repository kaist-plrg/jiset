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
    case IPush(expr, list) => IPush(walk(expr), walk(list))
    case IReturn(expr) => IReturn(walk(expr))
    case IIf(cond, thenInst, elseInst) => IIf(walk(cond), walk(thenInst), walk(elseInst))
    case IWhile(cond, body) => IWhile(walk(cond), walk(body))
    case ISeq(insts) => ISeq(walkList[Inst](insts, walk))
    case IAssert(expr) => IAssert(walk(expr))
    case IPrint(expr) => IPrint(walk(expr))
  }

  // expressions
  def walk(expr: Expr): Expr = expr match {
    case ENum(_) | EINum(_) | EStr(_) | EBool(_) | EUndef | ENull | EAbsent => expr
    case EMap(ty, props) => EMap(
      walk(ty),
      walkList[(Expr, Expr)](props, { case (x, y) => (walk(x), walk(y)) })
    )
    case EList(exprs) => EList(walkList[Expr](exprs, walk))
    case EPop(list) => EPop(walk(list))
    case ERef(ref) => ERef(walk(ref))
    case EFunc(params, varparam, body) => EFunc(walkList[Id](params, walk), walkOpt[Id](varparam, walk), walk(body))
    case EApp(fexpr, args) => EApp(walk(fexpr), walkList[Expr](args, walk))
    case EUOp(uop, expr) => EUOp(walk(uop), walk(expr))
    case EBOp(bop, left, right) => EBOp(walk(bop), walk(left), walk(right))
    case EExist(ref) => EExist(walk(ref))
    case ETypeOf(expr) => ETypeOf(walk(expr))
    case EIsInstanceOf(base, name) => EIsInstanceOf(walk(base), walk(name))
    case EGetSyntax(base) => EGetSyntax(walk(base))
    case EParseSyntax(code, rule) => EParseSyntax(walk(code), walk(rule))
    case EContains(list, elem) => EContains(walk(list), walk(elem))
    case ENotYetImpl(msg) => ENotYetImpl(walk(msg))
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

  ////////////////////////////////////////////////////////////////////////////////
  // States
  ////////////////////////////////////////////////////////////////////////////////

  // states
  def walk(st: State): State = State(
    walk(st.context),
    walkOpt[Value](st.retValue, walk),
    walkList[Inst](st.insts, walk),
    walkMap[Id, Value](st.globals, walk, walk),
    walkMap[Id, Value](st.locals, walk, walk),
    walk(st.heap)
  )

  // heaps
  def walk(heap: Heap): Heap = Heap(
    walkMap[Addr, Obj](heap.map, walk, walk),
    heap.size
  )

  // objects
  def walk(obj: Obj): Obj = obj match {
    case Singleton(name) => Singleton(walk(name))
    case CoreMap(ty, props) => CoreMap(
      walk(ty),
      walkMap[Value, Value](props, walk, walk)
    )
    case CoreList(values) => CoreList(
      walkList[Value](values.toList, walk).toVector
    )
  }

  // values
  def walk(value: Value): Value = value match {
    case addr: Addr => walk(addr)
    case ast: ASTVal => walk(ast)
    case ASTMethod(func, locals) => ASTMethod(walk(func), walkMap[Id, Value](locals, walk, walk))
    case func: Func => walk(func)
    case Num(_) | INum(_) | Str(_) | Bool(_) | Undef | Null | Absent => value
  }

  // addresses
  def walk(addr: Addr): Addr = addr

  // function
  def walk(func: Func): Func = func match {
    case Func(name, params, varparam, body) =>
      Func(walk(name), walkList[Id](params, walk), walkOpt[Id](varparam, walk), walk(body))
  }

  // AST values
  def walk(ast: ASTVal): ASTVal = ast

  // reference values
  def walk(refV: RefValue): RefValue = refV match {
    case RefValueId(id) => RefValueId(walk(id))
    case RefValueProp(addr, value) => RefValueProp(walk(addr), walk(value))
    case RefValueAST(ast, name) => RefValueAST(walk(ast), walk(name))
    case RefValueToNumber(s) => RefValueToNumber(walk(s))
  }
}
