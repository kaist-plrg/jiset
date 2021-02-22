package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.analyzer.domain.ops._
import kr.ac.kaist.jiset.analyzer.domain.generator._
import kr.ac.kaist.jiset.analyzer.domain.combinator._
import kr.ac.kaist.jiset.{ LINE_SEP => endl }

object Beautifier {
  def beautify[T](x: T)(
    implicit
    append: (Appender, T) => Appender
  ): String = append(new Appender, x).toString

  type App[T] = (Appender, T) => Appender
  type Update = Appender => Appender
  class Appender(tab: String = "  ") {
    val sb: StringBuilder = new StringBuilder
    var k = 0
    def indent = tab * k
    override def toString: String = sb.toString
    def wrap(f: => Unit): Appender = {
      this >> "{" >> endl
      k += 1; f; k -= 1
      this :> "}"
    }
    def :>(str: String): Appender = this >> indent >> str
    def >>(str: String): Appender = { sb ++= str; this }
    def :>[T](x: T)(implicit app: App[T]): Appender =
      this >> indent >> x
    def >>[T](x: T)(implicit app: App[T]): Appender = app(this, x)
    def >>(f: Update): Appender = f(this)
  }
  def nothingApp[T]: App[T] = (app, t) => app

  // Scala value appender
  implicit lazy val stringApp: App[String] = _ >> _
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
      val AbsPrim.Elem(num, int, bigint,
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
  implicit lazy val acloApp: App[AbsClo] = simpleDomainApp(AbsClo, "λ")
  implicit lazy val acontApp: App[AbsCont] = simpleDomainApp(AbsCont, "κ")
  implicit lazy val aastApp: App[AbsAST] = setDomainApp(AbsAST)
  implicit lazy val avalueApp: App[AbsValue] =
    domainApp(AbsValue)((app, v) => {
      val AbsValue.Elem(addr, clo, cont, ast, prim) = v
      var udts = Vector[Update]()
      if (!addr.isBottom) udts :+= { _ >> addr }
      if (!clo.isBottom) udts :+= { _ >> clo }
      if (!cont.isBottom) udts :+= { _ >> cont }
      if (!ast.isBottom) udts :+= { _ >> ast }
      if (!prim.isBottom) udts :+= { _ >> prim }
      app >> udts.reduce((x, y) => _ >> x >> " | " >> y)
    })
  implicit lazy val arefvalueApp: App[AbsRefValue] =
    domainApp(AbsRefValue)((app, arefval) => {
      val AbsRefValue.Elem(id, prop, string) = arefval
      var udts = Vector[Update]()
      if (!id.isBottom) udts :+= { _ >> id }
      if (!prop._1.isBottom) udts :+= { _ >> prop._1 >> "." >> prop._2 }
      if (!string._1.isBottom) udts :+= { _ >> string._1 >> "." >> string._2 }
      app >> udts.reduce((x, y) => _ >> x >> " | " >> y)
    })
  implicit lazy val aobjApp: App[AbsObj] =
    domainApp(AbsObj)((app, obj) => {
      val AbsObj.Elem(symbol, map, list) = obj
      var udts = Vector[Update]()
      if (!symbol.isBottom) udts :+= {
        implicit val symbolApp = setDomainApp(AbsObj.SymbolD)
        _ >> "@" >> symbol
      }
      if (!map.isBottom) udts :+= {
        implicit val mapApp = pmapDomainApp(AbsObj.MapD)
        _ >> map
      }
      if (!list.isBottom) udts :+= {
        implicit val listApp = listDomainApp(AbsObj.ListD)
        _ >> list
      }
      app >> udts.reduce((x, y) => _ >> x >> " | " >> y)
    })
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
    domainApp(domain)((app, elem) => elem match {
      case Fixed(vector) if vector.size == 0 => app >> "[]"
      case Fixed(vector) =>
        app >> "[" >> vector.map[Update](v => _ >> v)
          .reduce((l, r) => _ >> l >> ", " >> r) >> "]"
      case Unfixed(v) =>
        app >> "[[" >> v >> "]]"
    })
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
