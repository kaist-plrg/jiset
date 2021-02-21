package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.LINE_SEP

// IR Beautifier
object Beautifier {
  // beautify
  def beautify(
    node: IRNode,
    indent: String,
    detail: Boolean,
    index: Boolean
  ): String = {
    val walker = new BeautifierWalker
    walker.sb = new StringBuilder
    walker.indent = LINE_SEP + indent
    walker.detail = detail
    walker.index = index
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

    // shows index of instructions
    var index: Boolean = false

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
    def detailWalk(inst: Inst): Unit =
      if (detail) walk(inst) else walk("...")

    // instructions
    override def walk(inst: Inst): Unit = {
      val k = inst.line
      if (index && k != -1) walk(s"($k) ")
      inst match {
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
        case IThrow(id) =>
          walk("throw "); walk(id)
        case IIf(cond, thenInst, elseInst) =>
          walk("if "); walk(cond); walk(" "); detailWalk(thenInst)
          walk(" else "); detailWalk(elseInst)
        case IWhile(cond, body) =>
          walk("while "); walk(cond); walk(" "); walk(body)
        case ISeq(insts) =>
          walk("{");
          if (insts.length > 0) {
            if (detail) { walkList[Inst](insts, walk); walk(indent); }
            else walk(" ... ")
          }
          walk("}")
        case IAssert(expr) =>
          walk("assert "); walk(expr)
        case IPrint(expr) =>
          walk("print "); walk(expr)
        case IApp(id, fexpr, args) =>
          walk("app "); walk(id); walk(" = ("); walk(fexpr);
          walk(" "); walkListSep[Expr](args, " ", walk); walk(")")
        case IAccess(id, bexpr, expr) =>
          walk("access "); walk(id); walk(" = ("); walk(bexpr); walk(" "); walk(expr); walk(")")
        case IWithCont(id, params, inst) =>
          walk("withcont "); walk(id); walk(" (");
          walkListSep[Id](params, ", ", walk); walk(") ="); detailWalk(inst)
        case ISetType(expr, ty) =>
          walk("set-type "); walk(expr); walk(" "); walk(ty)
      }
    }

    // expressions
    override def walk(expr: Expr): Unit = expr match {
      case ENum(n) => walk(s"$n")
      case EINum(n) => walk(s"${n}i")
      case EBigINum(b) => walk(s"${b}n")
      case EStr(str) => walk("\"" + norm(str) + "\"")
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
      case ECont(params, body) =>
        walk("("); walkListSep[Id](params, ", ", walk);
        walk(") [=>] ")
        detailWalk(body)
      case EUOp(uop, expr) =>
        walk("("); walk(uop); walk(" "); walk(expr); walk(")")
      case EBOp(bop, left, right) =>
        walk("("); walk(bop); walk(" "); walk(left); walk(" "); walk(right); walk(")")
      case ETypeOf(expr) =>
        walk("(typeof "); walk(expr); walk(")")
      case EIsCompletion(expr) =>
        walk("(is-completion "); walk(expr); walk(")")
      case EIsInstanceOf(base, name) =>
        walk("(is-instance-of "); walk(base); walk(" "); walk(name); walk(")")
      case EGetElems(base, name) =>
        walk("(get-elems "); walk(base); walk(" "); walk(name); walk(")")
      case EGetSyntax(base) =>
        walk("(get-syntax "); walk(base); walk(")")
      case EParseSyntax(code, rule, flags) =>
        walk("(parse-syntax "); walk(code); walk(" "); walk(rule); walk(" "); walk(flags); walk(")")
      case EConvert(expr, cop, list) =>
        walk("(convert "); walk(expr); walk(" "); walk(cop); walk(" "); walkListSep[Expr](list, " ", walk); walk(")")
      case EContains(list, elem) =>
        walk("(contains "); walk(list); walk(" "); walk(elem); walk(")")
      case EReturnIfAbrupt(expr, check) =>
        if (check) walk("[? ") else walk("[! "); walk(expr); walk("]")
      case ECopy(obj) =>
        walk("(copy-obj "); walk(obj); walk(")")
      case EKeys(obj) =>
        walk("(map-keys "); walk(obj); walk(")")
      case ENotSupported(msg) =>
        walk("??? \""); walk(norm(msg)); walk("\"")
    }

    // references
    override def walk(ref: Ref): Unit = ref match {
      case RefId(id) => walk(id)
      case RefProp(ref, EStr(str)) =>
        walk(ref); walk("."); walk(str)
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
      case OUMod => "%%"
      case OMod => "%"
      case OEq => "="
      case OEqual => "=="
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

    // convert operators
    override def walk(cop: COp): Unit = walk(cop match {
      case CStrToNum => "str2num"
      case CStrToBigInt => "str2bigint"
      case CNumToStr => "num2str"
      case CNumToInt => "num2int"
      case CNumToBigInt => "num2bigint"
      case CBigIntToNum => "bigint2num"
    })
  }
}
