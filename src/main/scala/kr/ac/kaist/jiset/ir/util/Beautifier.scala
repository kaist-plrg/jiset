package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.ir._

// IR Beautifier
object Beautifier {
  import Appender._

  private var indent: String = ""
  private var detail: Boolean = true
  private var index: Boolean = false
  private var exprId: Boolean = false

  // visible length when `detail` is false
  private val VISIBLE_LENGTH = 10

  // tab string
  val TAB = "  "

  def beautify[T <: IRNode](
    node: T,
    indent: String = "",
    detail: Boolean = true,
    index: Boolean = false,
    exprId: Boolean = false
  )(
    implicit
    append: (Appender, T) => Appender
  ): String = {
    this.indent = LINE_SEP + indent
    this.detail = detail
    this.index = index
    this.exprId = exprId
    append(new Appender, node).toString
  }

  // irnode
  implicit lazy val IRNodeApp: App[IRNode] = (app, node) => {
    node match {
      case node: Inst => InstApp(app, node)
      case node: Expr => ExprApp(app, node)
      case node: Ref => RefApp(app, node)
      case node: Ty => TyApp(app, node)
      case node: Id => IdApp(app, node)
      case node: UOp => UOpApp(app, node)
      case node: BOp => BOpApp(app, node)
      case node: COp => COpApp(app, node)
    }
  }

  // lists
  def ListApp[T <: IRNode](
    app: Appender,
    list: List[T]
  ): Appender = {
    val oldIndent = indent
    indent = indent + TAB
    list.foreach { case t => app >> indent >> t }
    indent = oldIndent
    app
  }

  // lists with separator
  def ListSepApp[T <: IRNode](
    app: Appender,
    list: List[T],
    sep: String
  ): Appender = list match {
    case Nil => app
    case h :: t =>
      t.foldLeft(app >> h)((app, e) => app >> sep >> e)
  }

  // map with separator
  def MapSepApp[T <: (IRNode, IRNode)](
    app: Appender,
    map: List[T],
    sep: String
  ): Appender = map match {
    case Nil => app
    case h :: t =>
      t.foldLeft(app >> h._1 >> " -> " >> h._2)((app, e) =>
        app >> sep >> e._1 >> " -> " >> e._2)
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Syntax
  ////////////////////////////////////////////////////////////////////////////////
  def detailApp(app: Appender, inst: Inst): Appender =
    if (detail) app >> inst else app >> "..."

  // instructions
  implicit lazy val InstApp: App[Inst] = (app, inst) => {
    val k = inst.line
    if (index && k != -1) app >> s"$k:"
    inst match {
      case IExpr(expr) => app >> expr
      case ILet(id, expr) => app >> "let " >> id >> " = " >> expr
      case IAssign(ref, expr) => app >> ref >> " = " >> expr
      case IDelete(ref) => app >> "delete " >> ref
      case IAppend(expr, list) => app >> "append " >> expr >> " -> " >> list
      case IPrepend(expr, list) => app >> "prepend " >> expr >> " -> " >> list
      case IReturn(expr) => app >> "return " >> expr
      case IThrow(id) => app >> "throw " >> id
      case IIf(cond, thenInst, elseInst) =>
        app >> "if " >> cond >> " "
        detailApp(app, thenInst) >> " else "
        detailApp(app, elseInst)
      case IWhile(cond, body) => app >> "while " >> cond >> " " >> body
      case ISeq(insts) =>
        app >> "{"
        if (insts.length > 0) {
          if (detail) { ListApp[Inst](app, insts) >> indent }
          else app >> " ... "
        }
        app >> "}"
      case IAssert(expr) => app >> "assert " >> expr
      case IPrint(expr) => app >> "print " >> expr
      case IApp(id, fexpr, args) =>
        app >> "app " >> id >> " = (" >> fexpr >> " "
        ListSepApp[Expr](app, args, " ") >> ")"
      case IAccess(id, bexpr, expr) =>
        app >> "access " >> id >> " = (" >> bexpr >> " " >> expr >> ")"
      case IWithCont(id, params, inst) =>
        app >> "withcont " >> id >> " ("
        ListSepApp[Id](app, params, ", ") >> ") ="
        detailApp(app, inst)
      case ISetType(expr, ty) => app >> "set-type " >> expr >> " " >> ty
    }
  }

  // expressions
  implicit lazy val ExprApp: App[Expr] = (app, expr) => {
    expr match {
      case expr: AllocExpr if exprId && expr.asite != -1 =>
        app >> s"(${expr.asite})"
      case _ =>
    }
    expr match {
      case ENum(n) => app >> s"$n"
      case EINum(n) => app >> s"${n}i"
      case EBigINum(b) => app >> s"${b}n"
      case EStr(str) => app >> "\"" + norm(str) + "\""
      case EBool(b) => app >> s"$b"
      case EUndef => app >> "undefined"
      case ENull => app >> "null"
      case EAbsent => app >> "absent"
      case EMap(ty, props) =>
        app >> "(new " >> ty >> "("
        MapSepApp[(Expr, Expr)](app, props, ", ") >> "))"
      case EList(exprs) =>
        app >> "(new ["
        ListSepApp[Expr](app, exprs, ", ") >> "])"
      case ESymbol(desc) => app >> "(new '" >> desc >> ")"
      case EPop(list, idx) => app >> "(pop " >> list >> " " >> idx >> ")"
      case ERef(ref) => app >> ref
      case ECont(params, body) =>
        app >> "("
        ListSepApp[Id](app, params, ", ") >> ") [=>] "
        detailApp(app, body)
      case EUOp(uop, expr) => app >> "(" >> uop >> " " >> expr >> ")"
      case EBOp(bop, left, right) =>
        app >> "(" >> bop >> " " >> left >> " " >> right >> ")"
      case ETypeOf(expr) => app >> "(typeof " >> expr >> ")"
      case EIsCompletion(expr) => app >> "(is-completion " >> expr >> ")"
      case EIsInstanceOf(base, name) =>
        app >> "(is-instance-of " >> base >> " " >> name >> ")"
      case EGetElems(base, name) =>
        app >> "(get-elems " >> base >> " " >> name >> ")"
      case EGetSyntax(base) => app >> "(get-syntax " >> base >> ")"
      case EParseSyntax(code, rule, flags) =>
        app >> "(parse-syntax " >> code >> " " >> rule >> " " >> flags >> ")"
      case EConvert(expr, cop, list) =>
        app >> "(convert " >> expr >> " " >> cop >> " "
        ListSepApp[Expr](app, list, " ") >> ")"
      case EContains(list, elem) =>
        app >> "(contains " >> list >> " " >> elem >> ")"
      case EReturnIfAbrupt(expr, check) =>
        if (check) app >> "[? " else app >> "[! "
        app >> expr >> "]"
      case ECopy(obj) => app >> "(copy-obj " >> obj >> ")"
      case EKeys(obj) => app >> "(map-keys " >> obj >> ")"
      case ENotSupported(msg) => app >> "??? \"" >> norm(msg) >> "\""
    }
  }

  // ref
  implicit lazy val RefApp: App[Ref] = (app, ref) => ref match {
    case RefId(id) => app >> id
    case RefProp(ref, EStr(str)) if !exprId => app >> ref >> "." >> str
    case RefProp(ref, expr) => app >> ref >> "[" >> expr >> "]"
  }

  // types
  implicit lazy val TyApp: App[Ty] = (app, ty) => app >> ty.name

  // identifiers
  implicit lazy val IdApp: App[Id] = (app, id) => app >> id.name

  // unary operators
  implicit lazy val UOpApp: App[UOp] = (app, uop) => app >> (uop match {
    case ONeg => "-"
    case ONot => "!"
    case OBNot => "~"
  })

  // binary operators
  implicit lazy val BOpApp: App[BOp] = (app, bop) => app >> (bop match {
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
  implicit lazy val COpApp: App[COp] = (app, cop) => app >> (cop match {
    case CStrToNum => "str2num"
    case CStrToBigInt => "str2bigint"
    case CNumToStr => "num2str"
    case CNumToInt => "num2int"
    case CNumToBigInt => "num2bigint"
    case CBigIntToNum => "bigint2num"
  })
}
