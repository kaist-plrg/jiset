package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.analyzer.domain.combinator._
import kr.ac.kaist.jiset.analyzer.domain.generator._
import kr.ac.kaist.jiset.analyzer.domain.ops._
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Appender._
import kr.ac.kaist.jiset.{ LINE_SEP => endl }

object Beautifier {
  // Scala value appender
  implicit lazy val strflatApp: App[StrFlat] = flatDomainApp(StrFlat, "string")

  // concrete value appender
  implicit lazy val primApp: App[Prim] = (app, prim) => app >> (prim match {
    case Num(n) => s"$n"
    case INum(n) => s"${n}i"
    case BigINum(b) => s"${b}n"
    case Str(str) => "\"" + str + "\""
    case Bool(b) => s"$b"
    case Undef => "undefined"
    case Null => "null"
    case Absent => "absent"
  })
  implicit lazy val astApp: App[ASTVal] = (app, ast) => app >> "☊(" >> ast.name >> ")"
  implicit lazy val addrApp: App[Addr] = (app, addr) => app >> "#" >> (addr match {
    case NamedAddr(name) => name
    case DynamicAddr(k) => k.toString
  })
  implicit lazy val tyApp: App[Ty] = (app, ty) => app >> ty.name
  implicit lazy val constApp: App[Const] =
    (app, const) => app >> "~" >> const.const >> "~"

  // abstract value appender
  implicit lazy val anumApp: App[AbsNum] = flatDomainApp(AbsNum, "num")
  implicit lazy val ainumApp: App[AbsINum] = flatDomainApp(AbsINum, "int")
  implicit lazy val abiginumApp: App[AbsBigINum] = flatDomainApp(AbsBigINum, "bigint")
  implicit lazy val astrApp: App[AbsStr] = flatDomainApp(AbsStr, "str")
  implicit lazy val aboolApp: App[AbsBool] = flatDomainApp(AbsBool, "bool")
  implicit lazy val aundefApp: App[AbsUndef] = simpleDomainApp(AbsUndef, "undef")
  implicit lazy val anullApp: App[AbsNull] = simpleDomainApp(AbsNull, "null")
  implicit lazy val aabsentApp: App[AbsAbsent] = simpleDomainApp(AbsAbsent, "?")
  implicit lazy val aprimApp: App[AbsPrim] =
    domainApp(AbsPrim)((app, v) => {
      val AbsPrim(num, int, bigint,
        str, bool, undef, nullval, absent) = v
      var udts = Vector[Update]()
      if (!num.isBottom) udts :+= { _ >> num }
      if (!int.isBottom) udts :+= { _ >> int }
      if (!bigint.isBottom) udts :+= { _ >> bigint }
      if (!str.isBottom) udts :+= { _ >> str }
      if (!bool.isBottom) udts :+= { _ >> bool }
      if (!undef.isBottom) udts :+= { _ >> undef }
      if (!nullval.isBottom) udts :+= { _ >> nullval }
      if (!absent.isBottom) udts :+= { _ >> absent }
      app >> udts.reduce((x, y) => _ >> x >> " | " >> y)
    })
  implicit lazy val aaddrApp: App[AbsAddr] = setDomainApp(AbsAddr)
  implicit lazy val atyApp: App[AbsTy] = setDomainApp(AbsTy)
  implicit lazy val aconstApp: App[AbsConst] = setDomainApp(AbsConst)
  implicit lazy val acloApp: App[AbsClo] = {
    import AbsClo._
    implicit val pairApp: App[Pair] = (app, pair) => {
      val Pair(t, u) = pair
      app >> "λ(" >> t >> ")"
      if (u != AbsEnv.Empty) app >> "[" >> u >> "]"
      else app
    }
    implicit val setApp = setDomainApp(AbsClo.SetD)
    _ >> _.set
  }
  implicit lazy val acontApp: App[AbsCont] = simpleDomainApp(AbsCont, "κ")
  implicit lazy val aastApp: App[AbsAST] = setDomainApp(AbsAST)
  implicit lazy val apureApp: App[AbsPure] =
    domainApp(AbsPure)((app, v) => {
      val AbsPure(addr, ty, const, clo, cont, ast, prim) = v
      var udts = Vector[Update]()
      if (!addr.isBottom) udts :+= { _ >> addr }
      if (!ty.isBottom) udts :+= { _ >> ty }
      if (!const.isBottom) udts :+= { _ >> const }
      if (!clo.isBottom) udts :+= { _ >> clo }
      if (!cont.isBottom) udts :+= { _ >> cont }
      if (!ast.isBottom) udts :+= { _ >> ast }
      if (!prim.isBottom) udts :+= { _ >> prim }
      app >> udts.reduce((x, y) => _ >> x >> " | " >> y)
    })
  implicit lazy val comptypeApp: App[CompletionType] = _ >> _.shortString
  implicit lazy val acompApp: App[AbsComp] =
    domainApp(AbsComp)((app, comp) => {
      var udts = Vector[Update]()
      comp.map.foreach {
        case (k, (v, t)) => udts :+= (app => {
          app >> k
          if (!(t === AbsConst(Const("empty")))) app >> ":" >> t
          app >> "(" >> v >> ")"
        })
      }
      app >> udts.reduce((x, y) => _ >> x >> " | " >> y)
    })
  implicit lazy val avalueApp: App[AbsValue] =
    domainApp(AbsValue)((app, v) => {
      val AbsValue(pure, comp) = v
      var udts = Vector[Update]()
      if (!pure.isBottom) udts :+= { _ >> pure }
      if (!comp.isBottom) udts :+= { _ >> comp }
      app >> udts.reduce((x, y) => _ >> x >> " | " >> y)
    })
  implicit lazy val arefvalueApp: App[AbsRefValue] = (app, arefval) => arefval match {
    case AbsRefValue.Top => app >> "⊤"
    case AbsRefValue.Bot => app >> "⊥"
    case AbsRefValue.Id(name) => app >> name
    case AbsRefValue.ObjProp(ty, addr, prop) =>
      var udts = Vector[Update]()
      if (!ty.isBottom) udts :+= { _ >> ty }
      if (!addr.isBottom) udts :+= { _ >> addr }
      if (udts.length > 1) app >> "("
      app >> udts.reduce((x, y) => _ >> x >> " | " >> y)
      if (udts.length > 1) app >> ")"
      app >> "[" >> prop >> "]"
    case AbsRefValue.StrProp(str, prop) => app >> str >> "[" >> prop >> "]"
  }
  implicit lazy val aobjApp: App[AbsObj] = (app, obj) => {
    import AbsObj._
    obj match {
      case Top => app >> "⊤"
      case Bot => app >> "⊥"
      case SymbolElem(desc) => app >> "@" >> desc
      case MapElem(parent, map) =>
        implicit val mapApp = pmapDomainApp(AbsObj.MapD)
        parent.foreach(app >> _ >> " ")
        app >> map
      case ListElem(list) =>
        implicit val listApp = listDomainApp(AbsObj.ListD)
        app >> list
    }
  }
  implicit lazy val aheapApp: App[AbsHeap] = {
    implicit val mapApp = mapDomainApp(AbsHeap.MapD)
    _ >> _.map
  }
  implicit lazy val aenvApp: App[AbsEnv] = {
    implicit val mapApp = pmapDomainApp(AbsEnv.MapD)
    _ >> _.map
  }
  implicit lazy val astateApp: App[AbsState] =
    emptyApp(AbsState)((app, st) => app.wrap {
      app :> "env: " >> st.env >> endl
      if (!st.heap.isBottom) app :> "heap: " >> st.heap >> endl
    })

  // SimpleDomain appender
  def simpleDomainApp[V](
    domain: SimpleDomain[V],
    name: String
  ): App[domain.Elem] = {
    import domain._
    (app, elem) => elem match {
      case Top => app >> name
      case Bot => app >> "⊥"
    }
  }

  // FlatDomain appender
  def flatDomainApp[V](domain: FlatDomain[V], name: String = "⊤")(
    implicit
    vFormat: App[V]
  ): App[domain.Elem] = {
    import domain._
    (app, elem) => elem match {
      case Top => app >> name
      case Bot => app >> "⊥"
      case Single(v) => app >> v
    }
  }

  // SetDomain appender
  def setDomainApp[V](domain: SetDomain[V], name: String = "⊤")(
    implicit
    vFormat: App[V]
  ): App[domain.Elem] = {
    import domain._
    (app, elem) => elem match {
      case Top => app >> name
      case VSet(set) if set.isEmpty => app >> "ɛ"
      case VSet(set) if set.size == 1 => app >> set.head
      case VSet(set) =>
        app >> "(" >> set.map[Update](v => _ >> v)
          .reduce((x, y) => _ >> x >> " | " >> y) >> ")"
    }
  }

  // OptionDomain appender
  def optionDomainApp[V](domain: OptionDomain[V, _])(
    implicit
    avFormat: App[domain.AbsV]
  ): App[domain.Elem] = (app, elem) => {
    app >> (if (elem.absent.isBottom) "!" else "?") >> " " >> elem.value
  }

  // MapDomain appender
  def mapDomainApp[K, V](domain: MapDomain[K, V, _])(
    implicit
    kFormat: App[K],
    avFormat: App[domain.AbsV]
  ): App[domain.Elem] = {
    import domain._
    domainApp(domain)((app, elem) => {
      if (elem.map.size == 0 && elem.default.isBottom) app >> "{}"
      else app.wrap {
        for ((k, v) <- elem.map) app :> k >> " -> " >> v >> endl
        if (!elem.default.isBottom)
          app :> "_ -> " >> elem.default >> endl
      }
    })
  }

  // PMapDomain appender
  def pmapDomainApp[K, V](domain: PMapDomain[K, V, _])(
    implicit
    kFormat: App[K],
    avFormat: App[domain.AbsV]
  ): App[domain.Elem] = {
    import domain._
    implicit val avoptApp = optionDomainApp(AbsVOpt)
    domainApp(domain)((app, elem) => {
      if (elem.map.size == 0 && elem.default.isAbsent) app >> "{}"
      else app.wrap {
        for ((k, v) <- elem.map) app :> k >> " -> " >> v >> endl
        if (!elem.default.isAbsent)
          app :> "_ -> " >> elem.default >> endl
      }
    })
  }

  // ListDomain appender
  def listDomainApp[V](domain: ListDomain[V, _])(
    implicit
    avFormat: App[domain.AbsV]
  ): App[domain.Elem] = {
    import domain._
    (app, elem) => elem match {
      case Bot => app >> "⊥"
      case ListElem(v) =>
        if (v.isBottom) app >> "[]"
        else app >> "[" >> v >> "]"
    }
  }

  // domain appender
  def domainApp[T](domain: AbsDomain[T])(
    givenApp: App[domain.Elem]
  ): App[domain.Elem] = (app, elem) => elem match {
    case domain.Top => app >> "⊤"
    case domain.Bot => app >> "⊥"
    case _ => givenApp(app, elem)
  }

  // EmptyValue domain appender
  def emptyApp[T](domain: AbsDomain[T] with EmptyValue)(
    givenApp: App[domain.Elem]
  ): App[domain.Elem] = domainApp(domain)((app, elem) => elem match {
    case domain.Empty => app >> "ɛ"
    case _ => givenApp(app, elem)
  })

  // pair appender
  implicit def pairApp[T, U](
    implicit
    tApp: App[T],
    uApp: App[U]
  ): App[(T, U)] = (app, pair) => {
    val (t, u) = pair
    app >> "(" >> t >> ", " >> u >> ")"
  }
}
