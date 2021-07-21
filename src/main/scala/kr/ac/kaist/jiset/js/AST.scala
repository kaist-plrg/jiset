package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.error._
import kr.ac.kaist.jiset.spec.grammar._
import kr.ac.kaist.jiset.util.{ Span, Pos }
import kr.ac.kaist.jiset.util.Useful.cached
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
  trait Compressed
  case class NormalCompressed(
    idx: Int,
    subs: Array[Option[Compressed]],
    params: List[Boolean],
    span: Span
  ) extends Compressed
  case class LexicalCompressed(str: String) extends Compressed
  def apply(data: Json): Option[Compressed] = data match {
    case arr if data.isArray =>
      val List(jIdx, jSubs, jParams, jSpan) = arr.asArray.get.toList
      val idx = jIdx.asNumber.get.toInt.get
      val subs = jSubs.asArray.get.toArray.map(AST(_))
      val params = jParams.asArray.get.toList.map(_.asBoolean.get)
      val List(sl, sc, el, ec) =
        jSpan.asArray.get.toList.map(_.asNumber.get.toInt.get)
      Some(NormalCompressed(idx, subs, params, Span(Pos(sl, sc), Pos(el, ec))))
    case str if data.isString => Some(LexicalCompressed(str.asString.get))
    case none if data.isNull => None
    case _ => ???
  }
}
