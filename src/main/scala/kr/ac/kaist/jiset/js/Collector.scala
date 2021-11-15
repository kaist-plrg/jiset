package kr.ac.kaist.jiset.js

import kr.ac.kaist.jiset.js.ast._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.ir._
import io.circe._, io.circe.generic.semiauto._, io.circe.generic.auto._
import io.circe.syntax._

case class Collector(script: Script) {
  // state
  private lazy val state = Initialize(script)
  private lazy val interp = new Interp(state, None)
  private var handledObjects: Set[Addr] = Initialize.initSt.heap.map.keySet.toSet

  interp.fixpoint

  // result
  type Result = (List[String], SimpleValue)
  var result: List[Result] = List()
  private def add(id: String, sv: SimpleValue): Unit = add(List(id), sv)
  private def add(path: List[String], sv: SimpleValue): Unit = { result ::= (path, sv) }
  try {
    handleVariable
    handleLet
  } catch {
    case e: Throwable => e.printStackTrace()
  }

  //////////////////////////////////////////////////////////
  // Helper Functions
  //////////////////////////////////////////////////////////

  // Json protocol
  implicit lazy val SimpleValueEncoder: Encoder[SimpleValue] = Encoder.instance {
    case Num(d) if d.isNegInfinity => Json.obj("neginf" -> "".asJson)
    case Num(d) if d.isPosInfinity => Json.obj("posinf" -> "".asJson)
    case Num(d) if d.isNaN => Json.obj("nan" -> "".asJson)
    case Num(d) if d equals -0.0 => Json.obj("negzero" -> "".asJson)
    case Num(d) => d.asJson
    case INum(l) => l.asJson
    case BigINum(b) => Json.obj("bigint" -> b.toString.asJson)
    case Str(s) => s.asJson
    case Bool(b) => b.asJson
    case Undef => Json.obj("undefined" -> "".asJson)
    case Null => Json.Null
    case Absent => error("absent can't be converted to Json value")
  }
  def toJson: String = result.asJson.noSpaces

  // handle variables
  private def handleVariable: Unit = for (x <- createdVars) {
    getValue(s"""$globalMap["$x"].Value""") match {
      case Absent => // handle global accessor property
      case sv: SimpleValue => add(x, sv)
      case (addr: Addr) => handleObject(addr, List(x))
      case _ =>
    }
  }

  // handle lexical variables
  private def handleLet: Unit = for (x <- createdLets) {
    getValue(s"""$lexRecord["$x"].BoundValue""") match {
      case sv: SimpleValue => add(x, sv)
      case (addr: Addr) => handleObject(addr, List(x))
      case _ =>
    }
  }

  // handle addresses
  private lazy val PREFIX_GLOBAL = "GLOBAL."
  private lazy val PREFIX_INTRINSIC = "INTRINSIC_"
  private def addrToName(addr: Addr): Option[String] = addr match {
    case a @ NamedAddr(name) if name.startsWith(PREFIX_GLOBAL) =>
      val str = name.substring(PREFIX_GLOBAL.length).replaceAll(s"#$PREFIX_GLOBAL", "")
      if (str.startsWith(PREFIX_INTRINSIC)) None
      else Some(str)
    case _ => None
  }
  private def handleObject(addr: Addr, path: List[String]): Unit = {
    (addr, handledObjects.contains(addr)) match {
      case (_: DynamicAddr, false) if addr != globalThis =>
        handledObjects += addr
        state(addr) match {
          case (_: IRMap) => handleProperty(addr, path)
          case _ =>
        }
      case _ =>
    }
  }

  // handle properties
  private def handleProperty(addr: Addr, path: List[String]): Unit = {
    val subMap = access(addr, Str("SubMap"))
    for (p <- getStrKeys(subMap)) access(subMap, Str(p)) match {
      case addr: Addr => state(addr) match {
        case IRMap(Ty("DataProperty"), props, _) =>
          for {
            (value, _) <- props.get(Str("Value"))
          } value.escaped match {
            case sv: SimpleValue => add(path ++ List(p), sv)
            case addr: Addr => handleObject(addr, path ++ List(p))
            case _ =>
          }
        case x =>
      }
      case _ =>
    }
  }

  // get values
  def getValue(str: String): Value =
    getValue(Parser.parse[Expr](str)).escaped
  def getValue(expr: Expr): Value =
    (new Interp(state.copied, None)).interp(expr)
  def getValue(refV: RefValue): Value = state(refV)
  def getValue(addr: Addr, prop: String): Value =
    getValue(RefValueProp(addr, Str(prop)))

  // access properties
  private def access(base: Value, props: Value*): Value =
    props.foldLeft(base) {
      case (base, p) => state(base, p.escaped)
    }

  // get created variables
  private lazy val globalMap = "REALM.GlobalObject.SubMap"
  private lazy val globalThis = getValue(s"$globalMap.globalThis.Value")
  private lazy val createdVars: Set[String] = {
    val initial = getStrKeys(getValue("GLOBAL.SubMap"))
    val current = getStrKeys(getValue(globalMap))
    current -- initial
  }

  // get created lexical variables
  private lazy val lexRecord = "REALM.GlobalEnv.DeclarativeRecord.SubMap"
  private lazy val createdLets: Set[String] =
    getStrKeys(getValue(lexRecord))

  // get keys
  private def getStrKeys(value: Value): Set[String] =
    getKeys(value).collect { case Str(p) => p }
  private def getKeys(value: Value): Set[Value] = value match {
    case addr: Addr => state(addr) match {
      case (m: IRMap) => m.props.keySet.toSet
      case _ => Set()
    }
    case _ => Set()
  }

  // conversion to JS codes
  private def sv2str(sv: SimpleValue): String = sv match {
    case INum(n) => n.toString
    case sv => sv.toString
  }
  private def val2str(value: Value): Option[String] = value match {
    case sv: SimpleValue => Some(sv2str(sv))
    case addr: Addr => addrToName(addr) match {
      case Some(name) => Some(name)
      case None => None
    }
    case x => None
  }
}
