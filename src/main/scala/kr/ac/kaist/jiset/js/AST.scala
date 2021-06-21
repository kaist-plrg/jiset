package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.error._
import kr.ac.kaist.jiset.spec.grammar._
import kr.ac.kaist.jiset.util.{ Span, Pos }
import kr.ac.kaist.jiset.util.Useful.cached
import spray.json._

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
  def toJson: JsValue = JsArray(
    JsNumber(idx),
    JsArray(fullList.map {
      case (_, ASTVal(ast)) => ast.toJson
      case _ => JsNull
    }: _*),
    JsArray(parserParams.map(p => JsNumber(if (p) 1 else 0)): _*),
    JsArray(JsNumber(-1), JsNumber(-1), JsNumber(-1), JsNumber(-1)),
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

  // semantic map
  def semMap: Map[String, Algo] = AST.getSemMap((kind, idx))

  // get semantics
  def semantics(fname: String): Option[(Algo, List[Value])] = {
    semMap.get(fname + k.toString) match {
      case Some(f) => Some((f, ASTVal(this) :: list.map(_._2)))
      case None => semMap.get(fname + maxK.toString) match {
        case Some(f) => Some((f, ASTVal(this) :: fullList.map(_._2)))
        case None => list match {
          case List((_, ASTVal(x))) => x.semantics(fname)
          case _ => None
        }
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
}
