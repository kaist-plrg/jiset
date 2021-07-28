package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Appender._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.ir._

// IR Beautifier
class Beautifier(
  detail: Boolean = true,
  index: Boolean = false,
  asite: Boolean = false
) {
  // visible length when `detail` is false
  val VISIBLE_LENGTH = 10

  // pair appender
  implicit def pairApp[T, U](
    implicit
    tApp: App[T],
    uApp: App[U]
  ): App[(T, U)] = (app, pair) => {
    val (t, u) = pair
    app >> t >> " -> " >> u
  }

  // line info appender
  implicit def lineApp: App[Option[Int]] = (app, line) => line match {
    case None => app
    case Some(i) => app >> i
  }

  // IR nodes
  implicit lazy val IRNodeApp: App[IRNode] = (app, node) => node match {
    case node: Program => ProgramApp(app, node)
    case node: Inst => InstApp(app, node)
    case node: Expr => ExprApp(app, node)
    case node: Ref => RefApp(app, node)
    case node: Ty => TyApp(app, node)
    case node: Id => IdApp(app, node)
    case node: UOp => UOpApp(app, node)
    case node: BOp => BOpApp(app, node)
    case node: COp => COpApp(app, node)
    case node: State => StateApp(app, node)
    case node: Context => ContextApp(app, node)
    case node: Cursor => CursorApp(app, node)
    case node: Heap => HeapApp(app, node)
    case node: Obj => ObjApp(app, node)
    case node: Value => ValueApp(app, node)
    case node: RefValue => RefValueApp(app, node)
  }

  //////////////////////////////////////////////////////////////////////////////
  // Syntax
  //////////////////////////////////////////////////////////////////////////////
  // instrctions without detail information
  lazy val DetailInstApp: App[Inst] = (app, inst) => {
    if (detail) app >> inst else app >> "..."
  }

  // programs
  implicit lazy val ProgramApp: App[Program] = (app, program) => {
    program.insts.foldLeft(app)(_ :> _ >> LINE_SEP)
  }

  // instructions
  implicit lazy val InstApp: App[Inst] = (app, inst) => {
    inst.line match {
      case None =>
      case Some(i) => app >> s"$i:"
    }
    inst match {
      case IExpr(expr) => app >> expr
      case ILet(id, expr) => app >> "let " >> id >> " = " >> expr
      case IAssign(ref, expr) => app >> ref >> " = " >> expr
      case IDelete(ref) => app >> "delete " >> ref
      case IAppend(expr, list) => app >> "append " >> expr >> " -> " >> list
      case IPrepend(expr, list) => app >> "prepend " >> expr >> " -> " >> list
      case IReturn(expr) => app >> "return " >> expr
      case ithrow @ IThrow(id) =>
        if (asite) ithrow.asite match {
          case Some(k) => app >> "(" >> k >> ") "
          case _ =>
        }
        app >> "throw " >> id
      case IIf(cond, thenInst, elseInst) =>
        implicit val d = DetailInstApp
        app >> "if " >> cond >> " "
        app >> thenInst >> " else "
        app >> elseInst
      case IWhile(cond, body) => app >> "while " >> cond >> " " >> body
      case ISeq(insts) => app.listWrap(insts, detail)
      case IAssert(expr) => app >> "assert " >> expr
      case IPrint(expr) => app >> "print " >> expr
      case iapp @ IApp(id, fexpr, args) =>
        implicit val l = ListApp[Expr](sep = " ")
        if (asite) iapp.csite match {
          case Some(k) => app >> "(" >> k >> ") "
          case _ =>
        }
        app >> "app " >> id >> " = (" >> fexpr
        if (!args.isEmpty) app >> " " >> args
        app >> ")"
      case iaccess @ IAccess(id, bexpr, expr, args) =>
        if (asite) iaccess.csite match {
          case Some(k) => app >> "(" >> k >> ") "
          case _ =>
        }
        implicit val l = ListApp[Expr](sep = " ")
        app >> "access " >> id >> " = (" >> bexpr >> " " >> expr
        if (!args.isEmpty) app >> " " >> args
        app >> ")"
      case IClo(id, params, captured, body) =>
        implicit val d = DetailInstApp
        implicit val p = ListApp[Id](sep = ", ")
        app >> "clo " >> id >> " = (" >> params >> ")[" >> captured >> "] => " >> body
      case ICont(id, params, body) =>
        implicit val d = DetailInstApp
        implicit val l = ListApp[Id]("(", ", ", ")")
        app >> "cont " >> id >> " = " >> params >> " [=>] " >> body
      case IWithCont(id, params, inst) =>
        implicit val d = DetailInstApp
        implicit val l = ListApp[Id]("(", ", ", ")")
        app >> "withcont " >> id >> " " >> params >> " = " >> inst
    }
  }

  // expressions
  implicit lazy val ExprApp: App[Expr] = (app, expr) => {
    expr match {
      case expr: AllocExpr if asite => expr.asite match {
        case Some(k) => app >> s"(" >> k >> ") "
        case _ =>
      }
      case _ =>
    }
    expr match {
      case ENum(n) => app >> s"$n"
      case EINum(n) => app >> s"${n}i"
      case EBigINum(b) => app >> s"${b}n"
      case EStr(str) => app >> "\"" >> normStr(str) >> "\""
      case EBool(b) => app >> s"$b"
      case EUndef => app >> "undefined"
      case ENull => app >> "null"
      case EAbsent => app >> "absent"
      case EMap(ty, props) =>
        implicit val l = ListApp[(Expr, Expr)]("(", ", ", ")")
        app >> "(new " >> ty >> props >> ")"
      case EList(exprs) =>
        implicit val l = ListApp[Expr]("[", ", ", "]")
        app >> "(new " >> exprs >> ")"
      case ESymbol(desc) => app >> "(new '" >> desc >> ")"
      case EPop(list, idx) => app >> "(pop " >> list >> " " >> idx >> ")"
      case ERef(ref) => app >> ref
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
      case EParseSyntax(code, rule, parserParams) =>
        app >> "(parse-syntax " >> code >> " " >> rule >> " " >> parserParams >> ")"
      case EConvert(expr, cop, list) =>
        implicit val l = ListApp[Expr](sep = " ")
        app >> "(convert " >> expr >> " " >> cop
        for (l <- list) app >> " " >> l
        app >> ")"
      case EContains(list, elem) =>
        app >> "(contains " >> list >> " " >> elem >> ")"
      case EReturnIfAbrupt(expr, check) =>
        app >> "[" >> (if (check) "?" else "!") >> " " >> expr >> "]"
      case ECopy(obj) => app >> "(copy-obj " >> obj >> ")"
      case EKeys(obj, intSorted) =>
        app >> "(map-keys " >> obj
        if (intSorted) app >> " [int-sorted]"
        app >> ")"
      case ENotSupported(msg) => app >> "??? \"" >> normStr(msg) >> "\""
    }
  }

  // ref
  implicit lazy val RefApp: App[Ref] = (app, ref) => ref match {
    case RefId(id) => app >> id
    case RefProp(ref, EStr(str)) if !asite && "[_a-zA-Z0-9]+".r.matches(str) => app >> ref >> "." >> str
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

  //////////////////////////////////////////////////////////////////////////////
  // States
  //////////////////////////////////////////////////////////////////////////////
  // states
  implicit lazy val StateApp: App[State] = (app, st) => {
    val State(context, ctxtStack, globals, heap) = st
    app >> "context: " >> context >> LINE_SEP
    implicit val c = ListApp[String]("[", ", ", "]")
    app >> "context-stack: " >> ctxtStack.map(_.name) >> LINE_SEP
    app >> "globals: "
    app.listWrap(globals, detail) >> LINE_SEP
    app >> "heap: " >> heap
  }

  // contexts
  implicit lazy val ContextApp: App[Context] = (app, context) => app.wrap {
    val Context(cursor, retId, name, _, locals) = context
    app :> "name: " >> name >> LINE_SEP
    app :> "return: " >> retId >> LINE_SEP
    cursor match {
      case InstCursor(insts) => {
        app :> "insts: "
        if (detail) app.listWrap(insts) >> LINE_SEP
        else {
          val newInsts = (insts.slice(0, VISIBLE_LENGTH) ++
            (if (insts.length > VISIBLE_LENGTH) Some("...") else None))
          app.listWrap(insts, true) >> LINE_SEP
        }
      }
      case _ => ??? // TODO
    }
    app :> "local-vars: "
    app.listWrap(locals) >> LINE_SEP
  }

  // cursors
  implicit lazy val CursorApp: App[Cursor] = (app, cursor) => ??? // TODO

  // heaps
  implicit lazy val HeapApp: App[Heap] = (app, heap) => {
    val Heap(map, size) = heap
    app >> s"(SIZE = " >> size.toString >> "): "
    app.listWrap(map)
  }

  // objects
  implicit lazy val ObjApp: App[Obj] = (app, obj) => obj match {
    case IRSymbol(desc) => app >> "(Symbol " >> desc >> ")"
    case map @ IRMap(ty, _, _) => {
      app >> "(TYPE = " >> ty >> ") "
      app.listWrap(map.pairs)
    }
    case IRList(values) => {
      implicit val l = ListApp[Value]("[", ", ", "]")
      app >> values.toList
    }
    case IRNotSupported(tyname, msg) =>
      app >> "(NotSupported \"" >> tyname >> "\" \"" >> msg >> "\")"
  }

  // values
  implicit lazy val ValueApp: App[Value] = (app, v) => v match {
    case addr: Addr => AddrApp(app, addr)
    case ast: ASTVal => ASTValApp(app, ast)
    case func: Func => FuncApp(app, func)
    case clo: Clo => CloApp(app, clo)
    case cont: Cont => ContApp(app, cont)
    case Num(double) => app >> double.toString
    case INum(long) => app >> long.toString >> "i"
    case BigINum(bigint) => app >> bigint.toString >> "n"
    case Str(str) => app >> "\"" >> normStr(str) >> "\""
    case Bool(bool) => app >> bool.toString
    case Undef => app >> "undefined"
    case Null => app >> "null"
    case Absent => app >> "absent"
  }

  // addresses
  implicit lazy val AddrApp: App[Addr] = (app, addr) => addr match {
    case NamedAddr(name) => app >> "#" >> name
    case DynamicAddr(long) => app >> "#" >> long.toString
  }

  // AST values
  implicit lazy val ASTValApp: App[ASTVal] =
    (app, ast) => app >> "☊[" >> ast.ast.kind >> "](" >> ast.ast.toString >> ")"

  // functions
  implicit lazy val FuncApp: App[Func] = (app, func) => {
    val name = func.algo.name
    app >> "λ(" >> name >> ")"
  }

  // closures
  implicit lazy val CloApp: App[Clo] = (app, clo) => {
    implicit val p = ListApp[Id]("(", ", ", ")")
    implicit val l = ListApp[(Id, Value)]("[", ", ", "]")
    val Clo(_, params, locals, _) = clo
    app >> clo.name >> params >> locals.toList >> " => ..."
  }

  // continuations
  implicit lazy val ContApp: App[Cont] = (app, cont) => {
    implicit val l = ListApp[Id]("(", ", ", ")")
    val Cont(params, context, ctxtStack) = cont
    app >> context.name >> params >> " [=>] ..."
  }

  // reference values
  implicit lazy val RefValueApp: App[RefValue] = (app, refV) => refV match {
    case RefValueId(id) => app >> id
    case RefValueProp(addr, value) => app >> addr >> "[" >> value >> "]"
    case RefValueString(str, name) => app >> Str(str) >> "[" >> name >> "]"
  }
}
