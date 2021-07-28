package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.spec.algorithm.Algo
import scala.collection.mutable.{ Map => MMap }

// Walker for IR Language
trait UnitWalker {
  // all cases
  def walk(node: IRNode): Unit = node match {
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
    case ctxt: Context => walk(ctxt)
    case cursor: Cursor => walk(cursor)
  }

  // strings
  def walk(str: String): Unit = {}

  // booleans
  def walk(bool: Boolean): Unit = {}

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

  // mutable maps
  def walkMap[K, V](
    map: MMap[K, V],
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
    case IClo(id, params, captured, body) =>
      walk(id)
      walkList[Id](params, walk)
      walkList[Id](captured, walk)
      walk(body)
    case ICont(id, params, body) =>
      walk(id)
      walkList[Id](params, walk)
      walk(body)
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
    case EParseSyntax(code, rule, parserParams) =>
      walk(code); walk(rule); walk(parserParams)
    case EConvert(expr, cop, list) =>
      walk(expr); walk(cop); walkList[Expr](list, walk)
    case EContains(list, elem) =>
      walk(list); walk(elem)
    case EReturnIfAbrupt(expr, check) =>
      walk(expr)
    case ECopy(obj) =>
      walk(obj)
    case EKeys(obj, intSorted) =>
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
    walkList[Context](st.ctxtStack, walk)
    walkMap[Id, Value](st.globals, walk, walk)
    walk(st.heap)
  }

  def walk(ctxt: Context): Unit = {
    walk(ctxt.cursor)
    walk(ctxt.retId)
    walk(ctxt.name)
    walkMap[Id, Value](ctxt.locals, walk, walk)
  }

  // cursors
  def walk(cursor: Cursor): Unit = cursor match {
    case InstCursor(insts) => walkList[Inst](insts, walk)
    case NodeCursor(node) =>
  }

  // heaps
  def walk(heap: Heap): Unit = {
    walkMap[Addr, Obj](heap.map, walk, walk)
  }

  // objs
  def walk(obj: Obj): Unit = obj match {
    case IRSymbol(desc) =>
      walk(desc)
    case IRMap(ty, props, size) =>
      walk(ty)
      walkMap[Value, (Value, Long)](props, walk, (x) => walk(x._1))
    case IRList(values) =>
      walkList[Value](values.toList, walk)
    case IRNotSupported(tyname, msg) =>
      walk(tyname)
      walk(msg)
  }

  // values
  def walk(v: Value): Unit = v match {
    case addr: Addr => walk(addr)
    case ast: ASTVal => walk(ast)
    case func: Func => walk(func)
    case clo: Clo => walk(clo)
    case cont: Cont => walk(cont)
    case Num(_) | INum(_) | BigINum(_) | Str(_) | Bool(_) | Undef | Null | Absent =>
  }

  // addresses
  def walk(addr: Addr): Unit = {}

  // function
  def walk(func: Func): Unit = func match {
    case Func(algo) => walk(algo)
  }

  // algorithm
  def walk(algo: Algo): Unit = {}

  // closure
  def walk(clo: Clo): Unit = clo match {
    case Clo(ctxtName, params, locals, cursor) =>
      walk(ctxtName)
      walkList[Id](params, walk)
      walkMap[Id, Value](locals, walk, walk)
      walk(cursor)
  }

  // continuation
  def walk(cont: Cont): Unit = cont match {
    case Cont(params, context, ctxtStack) =>
      walkList[Id](params, walk)
      walk(context)
      walkList[Context](ctxtStack, walk)
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
