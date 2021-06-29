package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.spec.algorithm.Algo
import scala.collection.mutable.{ Map => MMap }

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
    case st: State => walk(st)
    case heap: Heap => walk(heap)
    case obj: Obj => walk(obj)
    case v: Value => walk(v)
    case refV: RefValue => walk(refV)
    case ctxt: Context => walk(ctxt)
  }

  // strings
  def walk(str: String): String = str

  // booleans
  def walk(bool: Boolean): Boolean = bool

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

  // mutable maps
  def walkMap[K, V](
    map: MMap[K, V],
    kWalk: K => K,
    vWalk: V => V
  ): MMap[K, V] = map.map { case (k, v) => kWalk(k) -> vWalk(v) }

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
      case IAccess(id, bexpr, expr, args) =>
        IAccess(walk(id), walk(bexpr), walk(expr), walkList[Expr](args, walk))
      case IWithCont(id, params, body) => IWithCont(walk(id), walkList[Id](params, walk), walk(body))
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
    case EClo(params, captured, body) =>
      EClo(walkList[Id](params, walk), walkList[Id](captured, walk), walk(body))
    case ECont(params, body) => ECont(walkList[Id](params, walk), walk(body))
    case EUOp(uop, expr) => EUOp(walk(uop), walk(expr))
    case EBOp(bop, left, right) => EBOp(walk(bop), walk(left), walk(right))
    case ETypeOf(expr) => ETypeOf(walk(expr))
    case EIsCompletion(expr) => EIsCompletion(walk(expr))
    case EIsInstanceOf(base, name) => EIsInstanceOf(walk(base), walk(name))
    case EGetElems(base, name) => EGetElems(walk(base), walk(name))
    case EGetSyntax(base) => EGetSyntax(walk(base))
    case EParseSyntax(code, rule, parserParams) =>
      EParseSyntax(walk(code), walk(rule), walk(parserParams))
    case EConvert(expr, cop, list) =>
      EConvert(walk(expr), walk(cop), walkList[Expr](list, walk))
    case EContains(list, elem) => EContains(walk(list), walk(elem))
    case EReturnIfAbrupt(expr, check) => EReturnIfAbrupt(walk(expr), check)
    case ECopy(obj) => ECopy(walk(obj))
    case EKeys(obj, intSorted) => EKeys(walk(obj), intSorted)
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

  ////////////////////////////////////////////////////////////////////////////////
  // States
  ////////////////////////////////////////////////////////////////////////////////

  // states
  def walk(st: State): State = State(
    walk(st.context),
    walkList[Context](st.ctxtStack, walk),
    walkMap[Id, Value](st.globals, walk, walk),
    walk(st.heap)
  )

  def walk(ctxt: Context): Context = Context(
    walk(ctxt.retId),
    walk(ctxt.name),
    walkList[Inst](ctxt.insts, walk),
    walkMap[Id, Value](ctxt.locals, walk, walk)
  )

  // heaps
  def walk(heap: Heap): Heap = Heap(walkMap[Addr, Obj](heap.map, walk, walk))

  // objects
  def walk(obj: Obj): Obj = obj match {
    case IRSymbol(desc) => IRSymbol(walk(desc))
    case IRMap(ty, props, size) => IRMap(
      walk(ty),
      walkMap[Value, (Value, Long)](props, walk, (x) => (walk(x._1), x._2)),
      size
    )
    case IRList(values) => IRList(
      walkList[Value](values.toList, walk).toVector
    )
    case IRNotSupported(tyname, msg) => IRNotSupported(walk(tyname), walk(msg))
  }

  // values
  def walk(value: Value): Value = value match {
    case addr: Addr => walk(addr)
    case ast: ASTVal => walk(ast)
    case ASTMethod(func, locals) => ASTMethod(walk(func), walkMap[Id, Value](locals, walk, walk))
    case func: Func => walk(func)
    case clo: Clo => walk(clo)
    case cont: Cont => walk(cont)
    case Num(_) | INum(_) | BigINum(_) | Str(_) | Bool(_) | Undef | Null | Absent => value
  }

  // addresses
  def walk(addr: Addr): Addr = addr

  // function
  def walk(func: Func): Func = func match {
    case Func(algo) => Func(walk(algo))
  }

  // algorithm
  def walk(algo: Algo): Algo = algo

  // closure
  def walk(clo: Clo): Clo = clo match {
    case Clo(ctxtName, params, locals, body) =>
      Clo(ctxtName, walkList[Id](params, walk), walkMap[Id, Value](locals, walk, walk), walk(body))
  }

  // continuation
  def walk(cont: Cont): Cont = cont match {
    case Cont(params, body, context, ctxtStack) =>
      Cont(walkList[Id](params, walk), walk(body), walk(context), walkList[Context](ctxtStack, walk))
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
