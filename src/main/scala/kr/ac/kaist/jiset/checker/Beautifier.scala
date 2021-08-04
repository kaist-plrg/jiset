package kr.ac.kaist.jiset.checker

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.ir
import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Appender._
import kr.ac.kaist.jiset.util.Useful._

// Checker Beautifier
object Beautifier {
  val irBeautifier = new ir.Beautifier(index = true)
  import irBeautifier._

  // type checker components
  implicit lazy val CheckerElemApp: App[CheckerElem] = (app, comp) => comp match {
    case comp: AbsSemantics => AbsSemanticsApp(app, comp)
    case comp: ControlPoint => ControlPointApp(app, comp)
    case comp: View => ViewApp(app, comp)
    case comp: AbsState => AbsStateApp(app, comp)
    case comp: AbsType => AbsTypeApp(app, comp)
    case comp: Type => TypeApp(app, comp)
    case comp: AbsRef => AbsRefApp(app, comp)
  }

  // abstract semantics
  implicit lazy val AbsSemanticsApp: App[AbsSemantics] = (app, sem) => {
    app >> sem.getString("")
  }

  // abstract references
  implicit lazy val AbsRefApp: App[AbsRef] = (app, ref) => ref match {
    case AbsId(x) => app >> x
    case AbsStrProp(base, str) => app >> base >> "." >> str
    case AbsGeneralProp(base, prop) => app >> base >> "[" >> prop >> "]"
  }

  // control points
  implicit lazy val ControlPointApp: App[ControlPoint] = (app, cp) => cp match {
    case NodePoint(node, view) => app >> node.simpleString >> ":" >> view
    case ReturnPoint(func, view) => app >> "RETURN:" >> view
  }

  // views
  implicit lazy val ViewApp: App[View] = (app, view) => {
    implicit val t = ListApp[Type]("[", ", ", "]")
    if (USE_VIEW) app >> view.tys
    else app >> "I"
  }

  // abstract states
  implicit lazy val AbsStateApp: App[AbsState] = (app, st) => {
    if (st.isBottom) app >> "⊥"
    else if (st.isEmpty) app >> "{}"
    else app.wrap {
      for ((x, t) <- st.map) app :> x >> " -> " >> t >> LINE_SEP
    }
  }

  // abstract types
  implicit lazy val AbsTypeApp: App[AbsType] = (app, aty) => {
    val set = aty.set
    if (set.size == 0) app >> "⊥"
    else if (set.size == 1) app >> set.head
    else {
      implicit val t = ListApp[String]("(", " | ", ")")
      app >> set.toList.map(_.beautified).sorted
    }
  }

  // types
  implicit lazy val TypeApp: App[Type] = (app, ty) => ty match {
    case ty: CompType => CompTypeApp(app, ty)
    case ty: PureType => PureTypeApp(app, ty)
  }

  // completion types
  implicit lazy val CompTypeApp: App[CompType] = (app, ty) => ty match {
    case NormalT(t) => app >> "Normal(" >> t >> ")"
    case AbruptT => app >> "Abrupt"
  }

  // pure types
  implicit lazy val PureTypeApp: App[PureType] = (app, ty) => ty match {
    case NameT(name) => app >> name
    case RecordT(props) if props.isEmpty => app >> "{}"
    case RecordT(props) => {
      implicit val p = ListApp[(String, AbsType)]("{ ", ", ", " }")
      app >> props.toList
    }
    case AstT(name) => app >> "☊(" >> name >> ")"
    case ConstT(name) => app >> "~" >> name >> "~"
    case FuncT(fid) => app >> "λ[" >> fid >> "]"
    case ESValueT => app >> "ESValue"
    case PrimT => app >> "prim"
    case ArithT => app >> "arith"
    case NumericT => app >> "numeric"
    case NumT => app >> "num"
    case BigIntT => app >> "bigint"
    case StrT => app >> "str"
    case BoolT => app >> "bool"
    case NilT => app >> "[]"
    case ListT(elem) => app >> "[" >> elem >> "]"
    case MapT(elem) => app >> s"{ _ |-> " >> elem >> " }"
    case SymbolT => app >> "symbol"
    case ANum(n) => app >> s"$n"
    case ABigInt(b) => app >> s"${b}n"
    case AStr(str) => app >> "\"" >> normStr(str) >> "\""
    case ABool(b) => app >> s"$b"
    case AUndef => app >> "undefined"
    case ANull => app >> "null"
    case AAbsent => app >> "?"
  }
}
