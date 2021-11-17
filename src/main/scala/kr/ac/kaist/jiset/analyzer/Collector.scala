package kr.ac.kaist.jiset.analyzer

// import kr.ac.kaist.jiset.ir._
import io.circe._, io.circe.generic.semiauto._, io.circe.generic.auto._, io.circe.syntax._
import kr.ac.kaist.jiset.{ error => Error, _ }
import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.ir
import kr.ac.kaist.jiset.js
import kr.ac.kaist.jiset.js.ast._
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.util.Appender._
import kr.ac.kaist.jiset.util.Useful._

case class Collector(script: Script, id: Int, start: Long) {
  import Collector._

  // state
  private val analysisStart = System.currentTimeMillis
  private val absSem =
    AbsSemantics(script, 0, timeLimit = Some(ANALYSIS_TIMEOUT)).fixpoint
  private val analysisTime = (System.currentTimeMillis - analysisStart) / 1000.0d
  private val elapsed = (System.currentTimeMillis - start) / 1000.0d
  private val finalResult = absSem.finalResult
  private val AbsRet(value, absState) = absSem.finalResult
  private var handledLocs: Set[SubMapLoc] = Set()

  // reachability
  val (pass, fail) =
    value.isAbruptCompletion.getSingle match {
      case FlatBot => (false, false)
      case FlatElem(ir.Bool(false)) => (true, false)
      case FlatElem(ir.Bool(true)) => (false, true)
      case FlatTop => (true, true)
    }

  // result
  private var result: CResult = CResult(elapsed, analysisTime, pass, fail)
  def toJson: String = Json.obj(
    "id" -> id.asJson,
    "time" -> Json.fromDouble((System.currentTimeMillis - start) / 1000.0d).get,
    "result" -> result.asJson
  ).noSpaces

  try {
    handleVariable
    handleLet
  } catch {
    case e: Throwable => e.printStackTrace()
  }

  //////////////////////////////////////////////////////////
  // Helper Functions
  //////////////////////////////////////////////////////////

  // handle variables
  private def handleVariable: Unit = for (x <- createdVars) {
    val absV = fixLoc(getValue(s"""$globalMap["$x"].Value"""))
    if (!absV.isBottom) {
      result.addEnv(x, absV)
      handleLoc(absV.loc)
    }
  }

  // handle lexical variables
  private def handleLet: Unit = for (x <- createdLets) {
    val absV = fixLoc(getValue(s"$lexRecord.$x.BoundValue"))
    if (!absV.isBottom) {
      result.addEnv(x, absV)
      handleLoc(absV.loc)
    }
  }

  // handle locations
  private def handleLoc(absLoc: AbsLoc): Unit = absLoc.getSingle match {
    case FlatElem(loc: SubMapLoc) => handleSubMapLoc(loc)
    case FlatBot =>
    case _ => error("should be single submap location")
  }
  private def handleSubMapLoc(subMapLoc: SubMapLoc): Unit = {
    if (!(handledLocs contains subMapLoc)) {
      handledLocs += subMapLoc
      val jsPropMap: Map[String, CValue] =
        (for {
          (jsPropStr, absV) <- getProps(subMapLoc)
          _ = handleLoc(absV.loc)
        } yield jsPropStr -> convertValue(absV)).toMap
      result.addHeap(subMapLoc, jsPropMap)
    }
  }

  // fix location in abstract value
  def fixLoc(absV: AbsValue): AbsValue = {
    val fixedLoc = absV.loc.getSingle match {
      case FlatElem(loc) => loc match {
        case base: BaseLoc => absState(base) match {
          case m: AbsObj.MapElem => AbsLoc(SubMapLoc(base))
          case _ => AbsLoc.Bot
        }
        case _ => error("should be base location")
      }
      case FlatBot => AbsLoc.Bot
      case FlatTop => AbsLoc.Top
    }
    absV.copy(loc = fixedLoc)
  }

  // get js properties
  private def getProps(loc: SubMapLoc): Map[String, AbsValue] = {
    val descMap =
      absState(loc) match {
        case AbsObj.KeyWiseMap(_, map) => map
        case AbsObj.OrderedMap(_, map, _) => map
        case AbsObj.MergedMap(_, prop, _) => ???
        case v => error(s"should be map object: $loc => $v")
      }
    (for {
      (propKey, desc) <- descMap
      propStr <- propKey match {
        case ASimple(ir.Str(str)) => Some(str)
        case _ => None
      }
      descLoc = desc.loc
      propV = absState(AbsValue(loc = descLoc), AbsValue("Value")).escaped
      fixedV = fixLoc(propV) if !fixedV.isBottom
    } yield propStr -> fixedV).toMap
  }

  // get values
  def getValue(str: String): AbsValue =
    getValue(ir.Parser.parse[ir.Expr](str)).escaped
  def getValue(expr: ir.Expr): AbsValue =
    absSem.transfer(absSem.runJobsRp, expr)

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
  private def getStrKeys(absV: AbsValue): Set[String] =
    getStrKeys(absV.loc)
  private def getStrKeys(absLoc: AbsLoc): Set[String] =
    getKeys(absLoc).collect { case ASimple(ir.Str(p)) => p }
  private def getKeys(absLoc: AbsLoc): Set[AValue] = absLoc match {
    case AbsLoc.Top => ???
    case AbsLoc.Base(locSet) => (for {
      loc <- locSet
      kv <- getKeys(loc)
    } yield kv).toSet
  }
  private def getKeys(loc: Loc): Set[AValue] = absState(loc) match {
    case AbsObj.KeyWiseMap(_, map) => map.keySet
    case AbsObj.OrderedMap(_, map, _) => map.keySet
    case AbsObj.MergedMap(_, prop, _) => ???
    case _ => Set()
  }
}

object Collector {
  // error result
  lazy val errorResult: String = Json.obj(
    "error" -> true.asJson
  ).asJson.noSpaces
  def toErrorJson(id: Int, start: Long): String = Json.obj(
    "id" -> id.asJson,
    "time" -> Json.fromDouble((System.currentTimeMillis - start) / 1000.0d).get,
    "result" -> errorResult.asJson
  ).noSpaces
  // convert loc to unique string
  def loc2str(loc: Loc): String = loc.hashCode.toString

  // result
  case class CResult(
    time: Double,
    analysisTime: Double,
    pass: Boolean,
    fail: Boolean,
    state: CState = CState()
  ) {
    def addEnv(id: String, cv: CValue): Unit = state.addEnv(id, cv)
    def addHeap(loc: SubMapLoc, map: Map[String, CValue]): Unit = state.addHeap(loc, map)
  }

  // state
  case class CState(
    var env: Map[String, CValue] = Map(),
    var heap: Map[String, Map[String, CValue]] = Map()
  ) {
    def addEnv(id: String, cv: CValue): Unit = { env += id -> cv }
    def addHeap(loc: SubMapLoc, map: Map[String, CValue]): Unit = {
      heap += loc2str(loc) -> map
    }
  }

  // flat element
  trait CFlat[+A]
  case object CFlatTop extends CFlat[Nothing]
  case object CFlatBot extends CFlat[Nothing]
  case class CFlatElem[A](elem: A) extends CFlat[A]

  // value
  case class CValue(
    absent: CFlat[Nothing] = CFlatBot,
    undefined: CFlat[Nothing] = CFlatBot,
    nullv: CFlat[Nothing] = CFlatBot,
    bool: CFlat[Boolean] = CFlatBot,
    num: CFlat[Double] = CFlatBot,
    bigint: CFlat[BigInt] = CFlatBot,
    str: CFlat[String] = CFlatBot,
    addr: CFlat[String] = CFlatBot
  )

  // convert AbsValue to CValue
  implicit def convertValue(absV: AbsValue): CValue = CValue(
    absent = absV.absent,
    undefined = absV.undef,
    nullv = absV.nullv,
    bool = absV.bool,
    num = convertNum(absV.num, absV.int),
    bigint = absV.bigint,
    str = absV.str,
    addr = absV.loc
  )
  implicit def convertAbsent(absV: AbsAbsent): CFlat[Nothing] = absV match {
    case AbsAbsent.Top => CFlatTop
    case AbsAbsent.Bot => CFlatBot
  }
  implicit def convertUndef(absV: AbsUndef): CFlat[Nothing] = absV match {
    case AbsUndef.Top => CFlatTop
    case AbsUndef.Bot => CFlatBot
  }
  implicit def convertNull(absV: AbsNull): CFlat[Nothing] = absV match {
    case AbsNull.Top => CFlatTop
    case AbsNull.Bot => CFlatBot
  }
  implicit def convertBool(absV: AbsBool): CFlat[Boolean] = absV match {
    case AbsBool.Bot => CFlatBot
    case AbsBool.Base(b) => CFlatElem(b.bool)
    case AbsBool.Top => CFlatTop
  }
  def convertNum(absNum: AbsNum, absInt: AbsInt): CFlat[Double] =
    (absNum.getSingle, absInt.getSingle) match {
      case (FlatTop, _) | (_, FlatTop) => CFlatTop
      case (FlatBot, FlatBot) => CFlatBot
      case (FlatBot, FlatElem(inum)) => CFlatElem(inum.long.toDouble)
      case (FlatElem(num), FlatBot) => CFlatElem(num.double)
      case (FlatElem(num), FlatElem(inum)) =>
        if (inum.long.toDouble equals num.double) CFlatElem(num.double)
        else CFlatTop
    }
  implicit def convertBigInt(absV: AbsBigInt): CFlat[BigInt] = absV match {
    case AbsBigInt.Bot => CFlatBot
    case AbsBigInt.Base(bigint) => CFlatElem(bigint.b)
    case AbsBigInt.Top => CFlatTop
  }
  implicit def convertStr(absV: AbsStr): CFlat[String] = absV.getSingle match {
    case FlatBot => CFlatBot
    case FlatElem(str) => CFlatElem(str.str)
    case FlatTop => CFlatTop
  }
  implicit def convertLoc(absV: AbsLoc): CFlat[String] = absV.getSingle match {
    case FlatBot => CFlatBot
    case FlatElem(loc: SubMapLoc) => CFlatElem(loc2str(loc))
    case FlatElem(base: BaseLoc) => error("")
    case FlatTop => CFlatTop
  }

  // JsonProtocol for CValue
  implicit lazy val DoubleEncoder: Encoder[Double] = new Encoder[Double] {
    final def apply(d: Double): Json = {
      if (d.isNegInfinity) Json.obj("neginf" -> "".asJson)
      else if (d.isPosInfinity) Json.obj("posinf" -> "".asJson)
      else if (d.isNaN) Json.obj("nan" -> "".asJson)
      else if (d equals -0.0) Json.obj("negzero" -> "".asJson)
      else Json.fromDouble(d).get
    }
  }
  implicit lazy val BigIntEncoder: Encoder[BigInt] = new Encoder[BigInt] {
    final def apply(b: BigInt): Json =
      Json.obj("bigint" -> b.toString.asJson)
  }
  implicit lazy val NothingEncoder: Encoder[Nothing] = new Encoder[Nothing] {
    final def apply(n: Nothing): Json = error("Nothing can't exist")
  }
  def mkFlatEncoder[A](implicit enc: Encoder[A]): Encoder[CFlat[A]] = Encoder.instance {
    case CFlatTop => Json.obj("TOP" -> Json.fromString(""))
    case CFlatBot => Json.obj("BOT" -> Json.fromString(""))
    case single: CFlatElem[A] => single.elem.asJson
  }
  implicit lazy val CFlatNothingEncoder: Encoder[CFlat[Nothing]] = mkFlatEncoder[Nothing]
  implicit lazy val CFlatBooleanEncoder: Encoder[CFlat[Boolean]] = mkFlatEncoder[Boolean]
  implicit lazy val CFlatDoubleEncoder: Encoder[CFlat[Double]] = mkFlatEncoder[Double]
  implicit lazy val CFlatBigIntEncoder: Encoder[CFlat[BigInt]] = mkFlatEncoder[BigInt]
  implicit lazy val CFlatStringEncoder: Encoder[CFlat[String]] = mkFlatEncoder[String]
  implicit lazy val CValueEncoder: Encoder[CValue] = new Encoder[CValue] {
    final def apply(cv: CValue): Json = Json.obj(
      "absent" -> cv.absent.asJson,
      "undefined" -> cv.undefined.asJson,
      "null" -> cv.nullv.asJson,
      "bool" -> cv.bool.asJson,
      "num" -> cv.num.asJson,
      "bigint" -> cv.bigint.asJson,
      "str" -> cv.str.asJson,
      "addr" -> cv.addr.asJson,
    )
  }
  implicit lazy val CStateEncoder: Encoder[CState] = deriveEncoder
  implicit lazy val CResultEncoder: Encoder[CResult] = deriveEncoder
}
