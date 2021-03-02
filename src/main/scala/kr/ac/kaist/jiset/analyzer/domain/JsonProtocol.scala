package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.analyzer.domain.generator._
import kr.ac.kaist.jiset.analyzer.domain.combinator._
import kr.ac.kaist.jiset.util.BasicJsonProtocol
import spray.json._

object JsonProtocol extends BasicJsonProtocol {
  // concrete value JSON format
  implicit lazy val numFormat = jsonFormat1(Num)
  implicit lazy val inumFormat = jsonFormat1(INum)
  implicit lazy val biginumFormat = jsonFormat1(BigINum)
  implicit lazy val strFormat = jsonFormat1(Str)
  implicit lazy val boolFormat = jsonFormat1(Bool)
  implicit lazy val astFormat = jsonFormat1(ASTVal)
  implicit object addrFormat extends RootJsonFormat[Addr] {
    def read(json: JsValue): Addr = json match {
      case JsString(str) => NamedAddr(str)
      case JsNumber(num) => DynamicAddr(num.toLong)
      case _ => deserializationError(s"unknown address: $json")
    }
    def write(addr: Addr): JsValue = addr match {
      case NamedAddr(name) => JsString(name)
      case DynamicAddr(long) => JsNumber(long)
    }
  }

  // abstract value JSON format
  implicit lazy val anumFormat = flatDomainFormat(AbsNum)
  implicit lazy val ainumFormat = flatDomainFormat(AbsINum)
  implicit lazy val abiginumFormat = flatDomainFormat(AbsBigINum)
  implicit lazy val astrFormat = flatDomainFormat(AbsStr)
  implicit lazy val aboolFormat = flatDomainFormat(AbsBool)
  implicit lazy val aundefFormat = simpleDomainFormat(AbsUndef)
  implicit lazy val anullFormat = simpleDomainFormat(AbsNull)
  implicit lazy val aabsentFormat = simpleDomainFormat(AbsAbsent)
  implicit lazy val aprimFormat = jsonFormat8(AbsPrim.Elem)
  implicit lazy val aastFormat = setDomainFormat(AbsAST)
  implicit lazy val acloFormat = new RootJsonFormat[AbsClo] {
    implicit val pairFormat: JsonFormat[AbsClo.Pair] = jsonFormat2(AbsClo.Pair)
    implicit val setFormat: JsonFormat[AbsClo.SetD] = setDomainFormat(AbsClo.SetD)
    def read(json: JsValue): AbsClo = AbsClo.Elem(setFormat.read(json))
    def write(clo: AbsClo): JsValue = setFormat.write(clo.set)
  }
  implicit lazy val acontFormat = simpleDomainFormat(AbsCont)
  implicit lazy val aaddrFormat = setDomainFormat(AbsAddr)
  implicit lazy val apureFormat = jsonFormat5(AbsPure.Elem)
  implicit lazy val acompFormat = new RootJsonFormat[AbsComp] {
    import AbsComp._
    val tyMap = CompletionType.tyMap
    def read(json: JsValue): Elem = Elem(json.asJsObject.fields.map {
      case (f, JsArray(Vector(jsV, jsT))) =>
        val v = apureFormat.read(jsV)
        val t = apureFormat.read(jsT)
        tyMap(f) -> (v, t)
      case _ => deserializationError(s"unknown element: $json")
    })
    def write(elem: Elem): JsValue = JsObject(elem.map.map {
      case (k, (v, t)) =>
        val jsV = apureFormat.write(v)
        val jsT = apureFormat.write(t)
        k.toString -> JsArray(jsV, jsT)
    })
  }
  implicit lazy val avalueFormat = jsonFormat2(AbsValue.Elem)
  implicit lazy val arefvalueFormat = new RootJsonFormat[AbsRefValue] {
    import AbsRefValue._
    implicit val IdFormat = jsonFormat1(Id)
    implicit val ObjPropFormat = jsonFormat2(ObjProp)
    implicit val StrPropFormat = jsonFormat2(StrProp)
    def read(json: JsValue): Elem = json match {
      case JsString("⊤") => Top
      case JsString("⊥") => Bot
      case v =>
        val discrimator = List("name", "addr", "str")
          .map(d => v.asJsObject.fields.contains(d))
        discrimator.indexOf(true) match {
          case 0 => IdFormat.read(v)
          case 1 => ObjPropFormat.read(v)
          case 2 => StrPropFormat.read(v)
          case _ => deserializationError(s"unknown element: $v")
        }
    }
    def write(elem: Elem): JsValue = elem match {
      case Top => JsString("⊤")
      case Bot => JsString("⊥")
      case (x: Id) => IdFormat.write(x)
      case (x: ObjProp) => ObjPropFormat.write(x)
      case (x: StrProp) => StrPropFormat.write(x)
    }
  }
  implicit lazy val aobjFormat: JsonFormat[AbsObj] = {
    implicit val symbolFormat = setDomainFormat(AbsObj.SymbolD)
    implicit val mapFormat = pmapDomainFormat(AbsObj.MapD)
    implicit val listFormat = listDomainFormat(AbsObj.ListD)
    jsonFormat3(AbsObj.Elem)
  }
  implicit lazy val aheapFormat: JsonFormat[AbsHeap] = new RootJsonFormat[AbsHeap] {
    implicit val mapFormat: JsonFormat[AbsHeap.MapD] = mapDomainFormat(AbsHeap.MapD)
    def read(json: JsValue): AbsHeap = AbsHeap.Elem(mapFormat.read(json))
    def write(heap: AbsHeap): JsValue = mapFormat.write(heap.map)
  }
  implicit lazy val aenvFormat: JsonFormat[AbsEnv] = new RootJsonFormat[AbsEnv] {
    implicit lazy val mapFormat: JsonFormat[AbsEnv.MapD] = pmapDomainFormat(AbsEnv.MapD)
    def read(json: JsValue): AbsEnv = AbsEnv.Elem(mapFormat.read(json))
    def write(env: AbsEnv): JsValue = mapFormat.write(env.map)
  }
  implicit lazy val astateFormat = jsonFormat2(AbsState.Elem)

  // SimpleDomain JSON format
  def simpleDomainFormat(domain: SimpleDomain[_]) = new RootJsonFormat[domain.Elem] {
    import domain._
    def read(json: JsValue): Elem = json match {
      case JsString("⊤") => Top
      case JsString("⊥") => Bot
      case _ => deserializationError(s"unknown element: $json")
    }
    def write(elem: Elem): JsValue = elem match {
      case Top => JsString("⊤")
      case Bot => JsString("⊥")
    }
  }

  // FlatDomain JSON format
  def flatDomainFormat[V](domain: FlatDomain[V])(
    implicit
    vFormat: JsonFormat[V]
  ) = new RootJsonFormat[domain.Elem] {
    import domain._
    def read(json: JsValue): Elem = json match {
      case JsString("⊤") => Top
      case JsString("⊥") => Bot
      case _ => Single(vFormat.read(json))
    }
    def write(elem: Elem): JsValue = elem match {
      case Top => JsString("⊤")
      case Bot => JsString("⊥")
      case Single(v) => vFormat.write(v)
    }
  }

  // SetDomain JSON format
  def setDomainFormat[V](domain: SetDomain[V])(
    implicit
    vFormat: JsonFormat[V]
  ) = new RootJsonFormat[domain.Elem] {
    import domain._
    def read(json: JsValue): Elem = json match {
      case JsString("⊤") => Top
      case JsArray(seq) => VSet(seq.map(vFormat.read).toSet)
      case _ => deserializationError(s"unknown element: $json")
    }
    def write(elem: Elem): JsValue = elem match {
      case Top => JsString("⊤")
      case VSet(set) => JsArray(set.toSeq.map(vFormat.write): _*)
    }
  }

  // OptionDomain JSON format
  def optionDomainFormat[V](domain: OptionDomain[V, _])(
    implicit
    avFormat: JsonFormat[domain.AbsV]
  ): JsonFormat[domain.Elem] = jsonFormat2(domain.Elem)

  // MapDomain JSON format
  def mapDomainFormat[K, V](domain: MapDomain[K, V, _])(
    implicit
    kFormat: JsonFormat[K],
    avFormat: JsonFormat[domain.AbsV]
  ): JsonFormat[domain.Elem] = {
    import domain._
    implicit val mapFormat = new RootJsonFormat[Map[K, AbsV]] {
      def read(json: JsValue): Map[K, AbsV] = json match {
        case JsObject(map) => map.map {
          case (k, v) => kFormat.read(k.parseJson) -> avFormat.read(v)
        }.toMap
        case _ => deserializationError(s"unknown element: $json")
      }
      def write(map: Map[K, AbsV]): JsValue = JsObject((map.toSeq.map {
        case (k, v) => kFormat.write(k).toString -> avFormat.write(v)
      }): _*)
    }
    jsonFormat2(Elem)
  }

  // PMapDomain JSON format
  def pmapDomainFormat[K, V](domain: PMapDomain[K, V, _])(
    implicit
    kFormat: JsonFormat[K],
    avFormat: JsonFormat[domain.AbsV]
  ): JsonFormat[domain.Elem] = {
    import domain._
    implicit val avoptFormat = optionDomainFormat(AbsVOpt)
    implicit val mapFormat = new RootJsonFormat[Map[K, AbsVOpt]] {
      def read(json: JsValue): Map[K, AbsVOpt] = json match {
        case JsObject(map) => map.map {
          case (k, v) => kFormat.read(k.parseJson) -> avoptFormat.read(v)
        }.toMap
        case _ => deserializationError(s"unknown element: $json")
      }
      def write(map: Map[K, AbsVOpt]): JsValue = JsObject((map.toSeq.map {
        case (k, v) => kFormat.write(k).toString -> avoptFormat.write(v)
      }): _*)
    }
    jsonFormat2(Elem)
  }

  // ListDomain JSON format
  def listDomainFormat[V](domain: ListDomain[V, _])(
    implicit
    avFormat: JsonFormat[domain.AbsV]
  ) = {
    import domain._
    val fixedFormat = jsonFormat1(Fixed)
    val unfixedFormat = jsonFormat1(Unfixed)
    new RootJsonFormat[Elem] {
      def read(json: JsValue): Elem = json match {
        case JsString("⊤") => Top
        case JsString("⊥") => Bot
        case _ =>
          val discrimator = List("fixed", "unfixed")
            .map(d => json.asJsObject.fields.contains(d))
          discrimator.indexOf(true) match {
            case 0 => fixedFormat.read(json)
            case 1 => unfixedFormat.read(json)
            case _ => deserializationError(s"unknown element: $json")
          }
      }
      def write(elem: Elem): JsValue = elem match {
        case Top => JsString("⊤")
        case Bot => JsString("⊥")
        case (x: Fixed) => fixedFormat.write(x)
        case (x: Unfixed) => unfixedFormat.write(x)
      }
    }
  }
}
