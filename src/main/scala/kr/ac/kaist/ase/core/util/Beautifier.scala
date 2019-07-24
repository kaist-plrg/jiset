package kr.ac.kaist.ase.core

import kr.ac.kaist.ase.LINE_SEP
import org.apache.commons.text.StringEscapeUtils

// CORE Beautifier
object Beautifier {
  // beautify
  def beautify(
    node: CoreNode,
    indent: String,
    detail: Boolean
  ): String = {
    val walker = new BeautifierWalker
    walker.sb = new StringBuilder
    walker.indent = LINE_SEP + indent
    walker.detail = detail
    walker.walk(node)
    walker.sb.toString
  }

  // walker for beautifier
  private class BeautifierWalker extends UnitWalker {
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
      case IAppend(expr, list) =>
        walk("append "); walk(expr); walk(" -> "); walk(list)
      case IPrepend(expr, list) =>
        walk("prepend "); walk(expr); walk(" -> "); walk(list)
      case IReturn(expr) =>
        walk("return "); walk(expr)
      case IIf(cond, thenInst, elseInst) =>
        walk("if "); walk(cond); walk(" "); walk(thenInst)
        walk(" else "); walk(elseInst)
      case IWhile(cond, body) =>
        walk("while "); walk(cond); walk(" "); walk(body)
      case ISeq(insts) =>
        walk("{");
        if (insts.length > 0) { walkList[Inst](insts, walk); walk(indent); }
        walk("}")
      case IAssert(expr) =>
        walk("assert "); walk(expr)
      case IPrint(expr) =>
        walk("print "); walk(expr)
      case IApp(id, fexpr, args) =>
        walk("app "); walk(id); walk(" = ("); walk(fexpr); walk(" "); walkListSep[Expr](args, " ", walk); walk(")")
      case IRef(id, ref) =>
        walk("ref "); walk(id); walk(" = "); walk(ref);
    }

    // expressions
    override def walk(expr: Expr): Unit = expr match {
      case ENum(n) => walk(s"$n")
      case EINum(n) => walk(s"${n}i")
      case EStr(str) => walk(s""""$str"""")
      case EBool(b) => walk(s"$b")
      case EUndef => walk("undefined")
      case ENull => walk("null")
      case EAbsent => walk("absent")
      case EMap(ty, props) =>
        walk("(new "); walk(ty); walk("("); walkListSep[(Expr, Expr)](props, ", ", {
          case (k, v) => walk(k); walk(" -> "); walk(v)
        }); walk("))")
      case EList(exprs) =>
        walk("(new ["); walkListSep[Expr](exprs, ", ", walk); walk("])");
      case ESymbol(desc) =>
        walk("(new '"); walk(desc); walk(")");
      case EPop(list, idx) =>
        walk("(pop "); walk(list); walk(" "); walk(idx); walk(")")
      case ERef(ref) => walk(ref)
      case EFunc(params, varparam, body) =>
        walk("("); walkListSep[Id](params, ", ", walk);
        walkOpt[Id](varparam, (id: Id) => if (params.length == 0) { walk("..."); walk(id); } else { walk(", ..."); walk(id) });
        walk(") => ")
        if (detail) walk(body)
        else walk("...")
      case EUOp(uop, expr) =>
        walk("("); walk(uop); walk(" "); walk(expr); walk(")")
      case EBOp(bop, left, right) =>
        walk("("); walk(bop); walk(" "); walk(left); walk(" "); walk(right); walk(")")
      case ETypeOf(expr) =>
        walk("(typeof "); walk(expr); walk(")")
      case EIsInstanceOf(base, name) =>
        walk("(is-instance-of "); walk(base); walk(" "); walk(name); walk(")")
      case EGetElems(base, name) =>
        walk("(get-elems "); walk(base); walk(" "); walk(name); walk(")")
      case EGetSyntax(base) =>
        walk("(get-syntax "); walk(base); walk(")")
      case EParseSyntax(code, rule, flags) =>
        walk("(parse-syntax "); walk(code); walk(" "); walk(rule); walk(" "); walkListSep[Expr](flags, " ", walk); walk(")")
      case EParseString(code, pop) =>
        walk("(parse-string "); walk(code); walk(" "); walk(pop); walk(")")
      case EConvert(expr, cop, list) =>
        walk("(convert "); walk(expr); walk(" "); walk(cop); walk(" "); walkListSep[Expr](list, " ", walk); walk(")")
      case EContains(list, elem) =>
        walk("(contains "); walk(list); walk(" "); walk(elem); walk(")")
      case ECopy(obj) =>
        walk("(copy-obj "); walk(obj); walk(")")
      case EKeys(obj) =>
        walk("(map-keys "); walk(obj); walk(")")
      case ENotYetImpl(msg) =>
        walk("??? \""); walk(msg); walk("\"")
      case ENotSupported(msg) =>
        walk("!!! \""); walk(msg); walk("\"")
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
      case OPow => "**"
      case ODiv => "/"
      case OMod => "%"
      case OEq => "="
      case OAnd => "&&"
      case OOr => "||"
      case OXor => "^^"
      case OBAnd => "&"
      case OBOr => "|"
      case OBXOr => "^"
      case OLShift => "<<"
      case OLt => "<"
      case OURShift => ">>>"
      case OSRShift => ">>"
    })

    // parse operators
    override def walk(pop: POp): Unit = walk(pop match {
      case PStr => "string"
      case PNum => "number"
      case PTVNoSubs => "tv-no-subs"
      case PTRVNoSubs => "trv-no-subs"
      case PTVHead => "tv-head"
      case PTRVHead => "trv-head"
      case PTVMiddle => "tv-middle"
      case PTRVMiddle => "trv-middle"
      case PTVTail => "tv-tail"
      case PTRVTail => "trv-tail"
    })

    // convert operators
    override def walk(cop: COp): Unit = walk(cop match {
      case CStrToNum => "str2num"
      case CNumToStr => "num2str"
      case CNumToInt => "num2int"
    })

    ////////////////////////////////////////////////////////////////////////////////
    // States
    ////////////////////////////////////////////////////////////////////////////////

    // states
    override def walk(st: State): Unit = oneDepth({
      val State(context, retValue, insts, globals, locals, heap) = st
      walk(indent); walk("Context: "); walk(context)
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
      case CoreSymbol(desc) =>
        walk("(Symbol "); walk(desc); walk(")")
      case CoreMap(ty, props) => oneDepth({
        walk("(TYPE = "); walk(ty); walk(")")
        walkMap[Value, Value](props, walk, walk)
      })
      case CoreList(values) => oneDepth({
        walk("(List [length = "); walk(values.length.toString); walk("])")
        walkList[Value](values.toList, walk)
      })
      case CoreNotSupported(msg) =>
        walk("(NotSupported \""); walk(msg); walk("\")")
    }

    // values
    override def walk(v: Value): Unit = v match {
      case addr: Addr => walk(addr)
      case ast: ASTVal => walk(ast)
      case ASTMethod(func, locals) =>
        walk("ASTMethod("); walk(func); walk(", ")
        walkMap[Id, Value](locals, walk, walk); walk(")")
      case func: Func => walk(func)
      case Num(double) => walk(s"$double")
      case INum(long) => walk(s"${long}i")
      case Str(str) => walk(s""""$str"""")
      case Bool(bool) => walk(s"$bool")
      case Undef => walk("undefined")
      case Null => walk("null")
      case Absent => walk("absent")
    }

    // addresses
    override def walk(addr: Addr): Unit = addr match {
      case NamedAddr(name) => walk(s"#$name")
      case DynamicAddr(long) => walk(s"#addr($long)")
    }

    // function
    override def walk(func: Func): Unit = func match {
      case Func(name, params, varparam, body) =>
        walk("\""); walk(name); walk("\" ("); walkListSep[Id](params, ", ", walk)
        walkOpt[Id](varparam, (id: Id) => if (params.length == 0) { walk("..."); walk(id); } else { walk(", ..."); walk(id) })
        walk(") => "); walk(body)
    }

    // AST values
    override def walk(ast: ASTVal): Unit = walk(s"${ast.ast}")

    // properties
    override def walk(refV: RefValue): Unit = refV match {
      case RefValueId(id) => walk(id)
      case RefValueProp(addr, value) =>
        walk(addr); walk("[\""); walk(value); walk("\"]")
      case RefValueAST(ast, name) =>
        walk(ast); walk("."); walk(name)
      case RefValueString(str, name) =>
        walk(str); walk("."); walk(name)
    }
  }
}
