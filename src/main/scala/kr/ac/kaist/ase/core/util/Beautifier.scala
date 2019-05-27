package kr.ac.kaist.ase.core

import kr.ac.kaist.ase.LINE_SEP

// CORE Beautifier
object Beautifier {
  // beautify
  def beautify(
    node: CoreNode,
    tab: String,
    detail: Boolean
  ): String = {
    BeautifierWalker.sb = new StringBuilder
    BeautifierWalker.indent = LINE_SEP
    BeautifierWalker.detail = detail
    BeautifierWalker.walk(node)
    BeautifierWalker.sb.toString
  }

  // walker for beautifier
  private object BeautifierWalker extends UnitWalker {
    // visible length when `detail` is false
    val VISIBLE_LENGTH = 10

    // string builder
    var sb: StringBuilder = null

    // current indentation
    var indent: String = null

    // option for detailed information
    var detail: Boolean = false

    // tab string
    val TAB = "  "

    // go one more depth
    def oneDepth(doit: => Unit): Unit = {
      val oldIndent = indent
      indent = indent + TAB
      doit
      indent = oldIndent
    }

    // strings
    override def walk(str: String): Unit = sb.append(str)

    // lists
    override def walkList[T](
      list: List[T],
      tWalk: T => Unit
    ): Unit = oneDepth(list.foreach {
      case t => sb.append(indent); tWalk(t)
    })

    // lists with separator
    def walkListSep[T](
      list: List[T],
      sep: String,
      tWalk: T => Unit
    ): Unit = list match {
      case Nil =>
      case _ =>
        list.foreach { case t => tWalk(t); walk(sep) }
        sb.delete(sb.length - sep.length, sb.length)
    }

    // maps
    override def walkMap[K, V](
      map: Map[K, V],
      kWalk: K => Unit,
      vWalk: V => Unit
    ): Unit = if (!map.isEmpty) oneDepth(map.foreach {
      case (k, v) => walk(indent); kWalk(k); walk(" -> "); vWalk(v)
    })

    // maps with separator
    def walkMapSep[K, V](
      map: Map[K, V],
      sep: String,
      kWalk: K => Unit,
      vWalk: V => Unit
    ): Unit = {
      map.foreach { case (k, v) => kWalk(k); walk(" -> "); vWalk(v); walk(sep) }
      sb.dropRight(sep.length)
    }

    ////////////////////////////////////////////////////////////////////////////////
    // Syntax
    ////////////////////////////////////////////////////////////////////////////////

    // programs
    override def walk(program: Program): Unit =
      walkList[Inst](program.insts, walk)

    // instructions
    override def walk(inst: Inst): Unit = inst match {
      case IExpr(expr) =>
        walk(expr)
      case ILet(id, expr) =>
        walk("let "); walk(id); walk(" = "); walk(expr)
      case IAssign(ref, expr) =>
        walk(ref); walk(" = "); walk(expr)
      case IDelete(ref) =>
        walk("delete "); walk(ref)
      case IPush(expr, list) =>
        walk("push "); walk(expr); walk(" -> "); walk(list)
      case IReturn(expr) =>
        walk("return "); walk(expr)
      case IIf(cond, thenInst, elseInst) =>
        walk("if "); walk(cond); walk(" "); walk(thenInst)
        walk(" else "); walk(elseInst)
      case IWhile(cond, body) =>
        walk("while "); walk(cond); walk(" "); walk(body)
      case ISeq(insts) =>
        walk("{"); walkList[Inst](insts, walk); walk(indent); walk("}")
      case IAssert(expr) =>
        walk("assert "); walk(expr)
      case IPrint(expr) =>
        walk("print "); walk(expr)
    }

    // expressions
    override def walk(expr: Expr): Unit = expr match {
      case ENum(n) => walk(s"$n")
      case EINum(n) => walk(s"i$n")
      case EStr(str) => walk(s""""$str"""")
      case EBool(b) => walk(s"$b")
      case EUndef => walk("undefined")
      case ENull => walk("null")
      case EMap(ty, props) =>
        walk("(new "); walk(ty); walk("("); walkListSep[(Expr, Expr)](props, ", ", {
          case (k, v) => walk(k); walk(" -> "); walk(v)
        }); walk("))")
      case EList(exprs) =>
        walk("(new ["); walkListSep[Expr](exprs, ", ", walk); walk("])");
      case EPop(list) =>
        walk("(pop "); walk(list); walk(")")
      case ERef(ref) => walk(ref)
      case EFunc(params, body) =>
        walk("("); walkListSep[Id](params, ", ", walk); walk(") => ")
        if (detail) walk(body)
        else walk("...")
      case EApp(fun, args) =>
        walk("("); walk(fun); walk(" "); walkListSep[Expr](args, " ", walk); walk(")")
      case ERun(id, name, args) =>
        walk("(run "); walk(name); walk(" of "); walk(id);
        if (args.length > 0) { walk(" with "); walkListSep[Expr](args, ", ", walk); }
        walk(")")
      case EUOp(uop, expr) =>
        walk("("); walk(uop); walk(" "); walk(expr); walk(")")
      case EBOp(bop, left, right) =>
        walk("("); walk(bop); walk(" "); walk(left); walk(" "); walk(right); walk(")")
      case EExist(ref) =>
        walk("(? "); walk(ref); walk(")")
      case ETypeOf(expr) =>
        walk("(typeof "); walk(expr); walk(")")
      case ENotYetImpl(msg) =>
        walk("??? \""); walk(msg); walk("\"")
    }

    // references
    override def walk(ref: Ref): Unit = ref match {
      case RefId(id) => walk(id)
      case RefProp(ref, expr) =>
        walk(ref); walk("["); walk(expr); walk("]")
    }

    // types
    override def walk(ty: Ty): Unit = walk(ty.name)

    // identifiers
    override def walk(id: Id): Unit = walk(id.name)

    // unary operators
    override def walk(uop: UOp): Unit = walk(uop match {
      case ONeg => "-"
      case ONot => "!"
      case OBNot => "~"
    })

    // binary operators
    override def walk(bop: BOp): Unit = walk(bop match {
      case OPlus => "+"
      case OSub => "-"
      case OMul => "*"
      case ODiv => "/"
      case OMod => "%"
      case OEq => "="
      case OAnd => "&&"
      case OOr => "||"
      case OBAnd => "&"
      case OBOr => "|"
      case OBXOr => "^"
      case OLShift => "<<"
      case OLt => "<"
      case OURShift => ">>>"
      case OSRShift => ">>"
    })

    ////////////////////////////////////////////////////////////////////////////////
    // States
    ////////////////////////////////////////////////////////////////////////////////

    // states
    override def walk(st: State): Unit = oneDepth({
      val State(retValue, insts, globals, locals, heap) = st
      walk(indent); walk("Return Value: "); walkOpt[Value](retValue, walk)
      walk(indent); walk("Instructions: ");
      if (detail) {
        walkList[Inst](insts, walk)
        walk(indent); walk("GlobalVars: "); walkMap[Id, Value](globals, walk, walk)
      } else {
        walkList[Inst](insts.slice(0, VISIBLE_LENGTH), walk)
        if (insts.length > VISIBLE_LENGTH) oneDepth({ walk(indent); walk("...") })
      }
      walk(indent); walk("LocalVars: "); walkMap[Id, Value](locals, walk, walk)
      walk(indent); walk("Heap"); walk(heap)
    })

    // heaps
    override def walk(heap: Heap): Unit = oneDepth({
      val Heap(map, size) = heap
      walk("(SIZE = "); walk(s"$size"); walk("):")
      walkMap[Addr, Obj](heap.map, walk, walk)
    })

    // objects
    override def walk(obj: Obj): Unit = obj match {
      case Singleton(name) =>
        walk("(Singleton "); walk(name); walk(")")
      case CoreMap(ty, props) => oneDepth({
        walk("(TYPE = "); walk(ty); walk(")")
        walkMap[Value, Value](props, walk, walk)
      })
      case CoreList(values) => oneDepth({
        walk("(List [length = "); walk(values.length.toString); walk(")")
        walkList[Value](values.toList, walk)
      })
    }

    // values
    override def walk(v: Value): Unit = v match {
      case addr: Addr => walk(addr)
      case Func(params, body) =>
        walk("("); walkListSep[Id](params, ", ", walk)
        walk(") => "); walk(body)
      case Num(double) => walk(s"$double")
      case INum(long) => walk(s"i$long")
      case Str(str) => walk(s""""$str"""")
      case Bool(bool) => walk(s"$bool")
      case Undef => walk("undefined")
      case Null => walk("null")
      case ASTVal(ast) => walk(s"$ast")
    }

    // addresses
    override def walk(addr: Addr): Unit = addr match {
      case NamedAddr(name) => walk(s"#$name")
      case DynamicAddr(long) => walk(s"#addr($long)")
    }

    // properties
    override def walk(refV: RefValue): Unit = refV match {
      case RefValueId(id) => walk(id)
      case RefValueProp(addr, value) =>
        walk(addr); walk("[\""); walk(value); walk("\"]")
    }
  }
}
