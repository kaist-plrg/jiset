package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.error._
import kr.ac.kaist.jiset.spec.grammar._
import kr.ac.kaist.jiset.util.{ Span, Pos }
import kr.ac.kaist.jiset.util.Useful.{ cached, error }
import io.circe._, io.circe.syntax._

trait AST {
  var parent: Option[AST] = None
  def kind: String
  def idx: Int
  def k: Int
  def span: Span
  def parserParams: List[Boolean]
  def fullList: List[(String, Value)]
  def maxK: Int

  // position
  var start: Int = 0
  var end: Int = 0

  // static semantics
  var staticMap: Map[String, Value] = Map()

  // name
  def name: String = kind + idx

  // to JSON format
  def toJson: Json = Json.arr(
    Json.fromInt(idx),
    Json.arr(fullList.map {
      case (_, ASTVal(ast)) => ast.toJson
      case _ => Json.Null
    }: _*),
    Json.arr(parserParams.map(p => Json.fromInt(if (p) 1 else 0)): _*),
    Json.arr(Json.fromInt(-1), Json.fromInt(-1), Json.fromInt(-1), Json.fromInt(-1))
  )

  // pretty printer
  def prettify: Json = Json.arr(
    Json.fromString(s"$kind[$idx,$k]"),
    Json.arr(fullList.map {
      case (_, ASTVal(ast)) => ast.prettify
      case _ => Json.Null
    }: _*),
    Json.arr(parserParams.map(Json.fromBoolean): _*),
  )

  // get possible kinds
  def getKinds: Set[String] = (list match {
    case List((_, ASTVal(ast))) => ast.getKinds
    case _ => Set()
  }) ++ Set(kind)

  // get element list for the given kind
  def getElems(given: String): List[AST] = {
    if (given == kind) List(this)
    else list.foldLeft(List[AST]()) {
      case (l, (_, ASTVal(ast))) => l ++ ast.getElems(given)
      case (l, _) => l
    }
  }

  // list of actual values
  lazy val list: List[(String, Value)] = fullList.filter {
    case (_, Absent) => false
    case _ => true
  }

  // children
  def children: List[Value] = list.map(_._2)

  // semantic map
  def semMap: Map[String, Algo] = AST.getSemMap((kind, idx))

  // get semantics
  def semantics(fname: String): Option[(Algo, List[Value])] = {
    semMap.get(fname + k.toString) match {
      case Some(f) => Some((f, ASTVal(this) :: fullList.map(_._2)))
      case None if fname == "Contains" => Some((defaultContains, List(ASTVal(this))))
      case None => list match {
        case List((_, ASTVal(x))) => x.semantics(fname)
        case _ => None
      }
    }
  }

  // existence check
  def exists(kindFilter: String => Boolean): Boolean = kindFilter(kind) || list.exists {
    case (_, ASTVal(ast)) => ast.exists(kindFilter)
    case _ => false
  }

  // get sub-AST
  def subs(name: String): Option[Value] = list.toMap.get(name)

  // Helpers
  protected def d(x: Any, n: Int): Int = x match {
    case Some(_) => 2 * n + 1
    case None => 2 * n
    case _ => n
  }
  protected def l(name: String, x: Any, list: List[(String, Value)]): List[(String, Value)] = x match {
    case Some(a: AST) => (name.substring(7, name.length - 1), ASTVal(a)) :: list
    case None => (name.substring(7, name.length - 1), Absent) :: list
    case a: AST => (name, ASTVal(a)) :: list
    case _ => list
  }

  // check supported syntax
  def checkSupported: AST = AST.checkSupported(this)
}
object AST {
  // check supported syntax
  private val notSupportedSyntaxPrefixList = List("RegularExpression")
  def checkSupported(ast: AST): AST = {
    ast.exists(name => notSupportedSyntaxPrefixList.exists(pre => {
      if (name.startsWith(pre)) throw NotSupported(pre)
      false
    }))
    ast
  }

  private def getSemMap = cached[(String, Int), Map[String, Algo]] {
    case (kind, idx) => {
      val pattern = s"${kind}\\[${idx},(\\d+)\\]\\.(.*)".r
      (for {
        (name, algo) <- algos
        methodName <- name match {
          case pattern(j, methodName) => Some(methodName + j)
          case _ => None
        }
      } yield methodName -> algo).toMap
    }
  }

  // compressed AST data
  trait Compressed {
    // equality of two compressed objects
    def equals(that: Compressed): Boolean = (this, that) match {
      case (c0: NormalCompressed, c1: NormalCompressed) =>
        // ignore span info
        val b0 = c0.idx == c1.idx
        val b1 = c0.subs.size == c1.subs.size &&
          c0.subs.zip(c1.subs).map {
            case (None, None) => true
            case (Some(s0), Some(s1)) => s0.equals(s1)
            case _ => false
          }.forall(_ == true)
        val b2 = c0.params.size == c1.params.size &&
          c0.params.zip(c1.params).map { case (p0, p1) => p0 == p1 }.forall(_ == true)
        b0 && b2
      case (LexicalCompressed(k0, s0), LexicalCompressed(k1, s1)) =>
        s0 == s1 && k0 == k1
      case _ => false
    }
  }
  case class NormalCompressed(
    idx: Int,
    subs: Array[Option[Compressed]],
    params: List[Boolean],
    span: Span
  ) extends Compressed
  case class LexicalCompressed(
    kind: String,
    str: String
  ) extends Compressed

  // convert json to compressed form
  def apply(data: Json): Option[Compressed] = data match {
    case arr if data.isArray => arr.asArray.get.toList match {
      // non-lexical
      case List(jIdx, jSubs, jParams, jSpan) =>
        val idx = jIdx.asNumber.get.toInt.get
        val subs = jSubs.asArray.get.toArray.map(AST(_))
        val params = jParams.asArray.get.toList.map(_.asNumber.get.toInt.get == 1)
        val List(sl, sc, el, ec) =
          jSpan.asArray.get.toList.map(_.asNumber.get.toInt.get)
        Some(NormalCompressed(idx, subs, params, Span(Pos(sl, sc), Pos(el, ec))))
      // lexical
      case List(jKind, jStr) =>
        val kind = jKind.asString.get
        val str = jStr.asString.get
        Some(LexicalCompressed(kind, str))
    }
    case none if data.isNull => None
    case _ => error("invalid AST compressed form")
  }
}
