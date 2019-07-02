package kr.ac.kaist.ase.core

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
    case pop: POp => walk(pop)
    case cop: COp => walk(cop)
    case st: State => walk(st)
    case heap: Heap => walk(heap)
    case obj: Obj => walk(obj)
    case v: Value => walk(v)
    case refV: RefValue => walk(refV)
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
    case IPush(expr, list) =>
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
  }

  // expressions
  def walk(expr: Expr): Unit = expr match {
    case ENum(_) | EINum(_) | EStr(_) | EBool(_) | EUndef | ENull | EAbsent =>
    case EMap(ty, props) =>
      walk(ty); walkList[(Expr, Expr)](props, { case (x, y) => (walk(x), walk(y)) })
    case EList(exprs) =>
      walkList[Expr](exprs, walk)
    case EPop(list) =>
      walk(list)
    case ERef(ref) =>
      walk(ref)
    case EFunc(params, varparam, body) =>
      walkList[Id](params, walk);
      walkOpt[Id](varparam, walk);
      walk(body)
    case EApp(fexpr, args) =>
      walk(fexpr); walkList[Expr](args, walk)
    case EUOp(uop, expr) =>
      walk(uop); walk(expr)
    case EBOp(bop, left, right) =>
      walk(bop); walk(left); walk(right)
    case EExist(ref) =>
      walk(ref)
    case ETypeOf(expr) =>
      walk(expr)
    case EIsInstanceOf(base, name) =>
      walk(base); walk(name)
    case EGetSyntax(base) =>
      walk(base)
    case EParseSyntax(code, rule) =>
      walk(code); walk(rule)
    case EParseString(code, pop) =>
      walk(code); walk(pop)
    case EConvert(expr, cop) =>
      walk(expr); walk(cop)
    case EContains(list, elem) =>
      walk(list); walk(elem)
    case ECopy(obj) =>
      walk(obj)
    case ENotYetImpl(msg) =>
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

  // parse operators
  def walk(pop: POp): Unit = {}

  // convert operators
  def walk(cop: COp): Unit = {}

  ////////////////////////////////////////////////////////////////////////////////
  // States
  ////////////////////////////////////////////////////////////////////////////////

  // states
  def walk(st: State): Unit = {
    walk(st.context)
    walkOpt[Value](st.retValue, walk)
    walkList[Inst](st.insts, walk)
    walkMap[Id, Value](st.globals, walk, walk)
    walkMap[Id, Value](st.locals, walk, walk)
    walk(st.heap)
  }

  // heaps
  def walk(heap: Heap): Unit = {
    walkMap[Addr, Obj](heap.map, walk, walk)
  }

  // objs
  def walk(obj: Obj): Unit = obj match {
    case Singleton(name) =>
      walk(name)
    case CoreMap(ty, props) =>
      walk(ty)
      walkMap[Value, Value](props, walk, walk)
    case CoreList(values) =>
      walkList[Value](values.toList, walk)
  }

  // values
  def walk(v: Value): Unit = v match {
    case addr: Addr => walk(addr)
    case ast: ASTVal => walk(ast)
    case ASTMethod(func, locals) =>
      walk(func); walkMap[Id, Value](locals, walk, walk)
    case func: Func => walk(func)
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

  // AST values
  def walk(ast: ASTVal): Unit = {}

  // properties
  def walk(refV: RefValue): Unit = refV match {
    case RefValueId(id) => walk(id)
    case RefValueProp(addr, value) =>
      walk(addr); walk(value)
    case RefValueAST(ast, name) =>
      walk(ast); walk(name)
  }
}
