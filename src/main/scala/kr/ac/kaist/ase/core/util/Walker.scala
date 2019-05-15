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
    case env: Env => walk(env)
    case heap: Heap => walk(heap)
    case obj: Obj => walk(obj)
    case v: Value => walk(v)
    case cont: Cont => walk(cont)
    case prop: Prop => walk(prop)
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
    case IExpr(lhs, expr) => IExpr(walk(lhs), walk(expr))
    case IAlloc(lhs, ty) => IAlloc(walk(lhs), walk(ty))
    case IDelete(ref) => IDelete(walk(ref))
    case IApp(lhs, fun, args) => IApp(walk(lhs), walk(fun), walkList[Expr](args, walk))
    case IReturn(expr) => IReturn(walk(expr))
    case IIf(cond, thenInst, elseInst) => IIf(walk(cond), walk(thenInst), walk(elseInst))
    case IWhile(cond, body) => IWhile(walk(cond), walk(body))
    case ITry(lhs, tryInst) => ITry(walk(lhs), walk(tryInst))
    case IThrow(expr) => IThrow(walk(expr))
    case ISeq(insts) => ISeq(walkList[Inst](insts, walk))
    case IAssert(expr) => IAssert(walk(expr))
    case IPrint(expr) => IPrint(walk(expr))
    case INotYetImpl(msg) => INotYetImpl(msg)
  }

  // expressions
  def walk(expr: Expr): Expr = expr match {
    case ERef(ref) => ERef(walk(ref))
    case EFunc(params, body) => EFunc(walkList[Id](params, walk), walk(body))
    case EUOp(uop, expr) => EUOp(walk(uop), walk(expr))
    case EBOp(bop, left, right) => EBOp(walk(bop), walk(left), walk(right))
    case EExist(ref) => EExist(walk(ref))
    case ETypeOf(expr) => ETypeOf(walk(expr))
    case _ => expr
  }

  // references
  def walk(ref: Ref): Ref = ref match {
    case RefId(id) => RefId(walk(id))
    case RefIdProp(ref, id) => RefIdProp(walk(ref), walk(id))
    case RefStrProp(ref, expr) => RefStrProp(walk(ref), walk(expr))
  }

  // left-hand-sides
  def walk(lhs: Lhs): Lhs = lhs match {
    case LhsRef(ref) => LhsRef(walk(ref))
    case LhsLet(id) => LhsLet(walk(id))
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
    walkList[Inst](st.insts, walk),
    walkMap[Id, Value](st.globals, walk, walk),
    walk(st.env),
    walk(st.heap)
  )

  // environments
  def walk(env: Env): Env = Env(
    walk(env.locals),
    walkOpt[Cont](env.retCont, walk),
    walkOpt[Cont](env.excCont, walk)
  )

  // heaps
  def walk(heap: Heap): Heap = Heap(
    walkMap[Addr, Obj](heap.map, walk, walk),
    heap.size
  )

  // objects
  def walk(obj: Obj): Obj = Obj(
    walk(obj.ty),
    walkMap[Id, Value](obj.idProps, walk, walk),
    walkMap[String, Value](obj.strProps, walk, walk)
  )

  // values
  def walk(value: Value): Value = value match {
    case addr: Addr => walk(addr)
    case Func(params, body) => Func(walkList[Id](params, walk), walk(body))
    case v => v
  }

  // addresses
  def walk(addr: Addr): Addr = addr

  // continuations
  def walk(cont: Cont): Cont = Cont(
    walk(cont.prop),
    walkList[Inst](cont.insts, walk),
    walk(cont.env)
  )

  // properties
  def walk(prop: Prop): Prop = prop match {
    case GlobalId(id) => GlobalId(walk(id))
    case PropId(addr, id) => PropId(walk(addr), walk(id))
    case PropStr(addr, str) => PropStr(walk(addr), str)
  }
}
