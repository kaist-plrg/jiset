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
    case st: State => walk(st)
    case env: Env => walk(env)
    case heap: Heap => walk(heap)
    case obj: Obj => walk(obj)
    case v: Value => walk(v)
    case cont: Cont => walk(cont)
    case prop: Prop => walk(prop)
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
    case IExpr(lhs, expr) =>
      walk(lhs); walk(expr)
    case IDelete(ref) =>
      walk(ref)
    case IReturn(expr) =>
      walk(expr)
    case IIf(cond, thenInst, elseInst) =>
      walk(cond); walk(thenInst); walk(elseInst)
    case IWhile(cond, body) =>
      walk(cond); walk(body)
    case ITry(lhs, tryInst) =>
      walk(lhs); walk(tryInst)
    case IThrow(expr) =>
      walk(expr)
    case ISeq(insts) =>
      walkList[Inst](insts, walk)
    case IAssert(expr) =>
      walk(expr)
    case IPrint(expr) =>
      walk(expr)
    case INotYetImpl(msg) =>
  }

  // expressions
  def walk(expr: Expr): Unit = expr match {
    case ERef(ref) =>
      walk(ref)
    case EFunc(params, body) =>
      walkList[Id](params, walk); walk(body)
    case EUOp(uop, expr) =>
      walk(uop); walk(expr)
    case EBOp(bop, left, right) =>
      walk(bop); walk(left); walk(right)
    case EExist(ref) => walk(ref)
    case ETypeOf(expr) => walk(expr)
    case _ =>
  }

  // references
  def walk(ref: Ref): Unit = ref match {
    case RefId(id) => walk(id)
    case RefIdProp(ref, id) =>
      walk(ref); walk(id)
    case RefStrProp(ref, expr) =>
      walk(ref); walk(expr)
  }

  // left-hand-sides
  def walk(lhs: Lhs): Unit = lhs match {
    case LhsRef(ref) => walk(ref)
    case LhsLet(id) => walk(id)
  }

  // types
  def walk(ty: Ty): Unit = {}

  // identifiers
  def walk(id: Id): Unit = {}

  // unary operators
  def walk(uop: UOp): Unit = {}

  // binary operators
  def walk(bop: BOp): Unit = {}

  ////////////////////////////////////////////////////////////////////////////////
  // States
  ////////////////////////////////////////////////////////////////////////////////

  // states
  def walk(st: State): Unit = {
    walkList[Inst](st.insts, walk)
    walkMap[Id, Value](st.globals, walk, walk)
    walk(st.env)
    walk(st.heap)
  }

  // environments
  def walk(env: Env): Unit = {
    walk(env.locals)
    walkOpt[Cont](env.retCont, walk)
    walkOpt[Cont](env.excCont, walk)
  }

  // heaps
  def walk(heap: Heap): Unit = {
    walkMap[Addr, Obj](heap.map, walk, walk)
  }

  // objects
  def walk(obj: Obj): Unit = {
    walk(obj.ty)
    walkMap[Id, Value](obj.idProps, walk, walk)
    walkMap[String, Value](obj.strProps, walk, walk)
  }

  // values
  def walk(v: Value): Unit = v match {
    case addr: Addr => walk(addr)
    case Func(params, body) =>
      walkList[Id](params, walk); walk(body)
    case _ =>
  }

  // addresses
  def walk(addr: Addr): Unit = {}

  // continuations
  def walk(cont: Cont): Unit = {
    walk(cont.prop)
    walkList[Inst](cont.insts, walk)
    walk(cont.env)
  }

  // properties
  def walk(prop: Prop): Unit = prop match {
    case GlobalId(id) => walk(id)
    case PropId(addr, id) =>
      walk(addr); walk(id)
    case PropStr(addr, str) => walk(addr)
  }
}
