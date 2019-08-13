package kr.ac.kaist.ase

import kr.ac.kaist.ase.core._

trait AST {
  val kind: String
  val name: String
  val k: Int
  val parserParams: List[Boolean]
  val info: ASTInfo
  val fullList: List[(String, Value)]

  // to JSON format
  def toJson: String = "{" + (fullList.map {
    case (name, value) => "\"" + name + "\":" + (value match {
      case ASTVal(ast) => ast.toJson
      case Str(str) => "\"" + str + "\""
      case _ => null
    })
  }).mkString(",") + "}"

  // get possible kinds
  def getKinds: Set[String] = (list match {
    case List((_, ASTVal(ast))) => ast.getKinds
    case _ => Set()
  }) ++ Set(kind)

  // get element list for the given kind
  def getElems(given: String): List[AST] = {
    if (given == kind) List(this)
    else (List[AST]() /: list) {
      case (l, (_, ASTVal(ast))) => l ++ ast.getElems(given)
      case (l, _) => l
    }
  }

  // list of actual values
  lazy val list: List[(String, Value)] = fullList.filter {
    case (_, Absent) => false
    case _ => true
  }

  // get semantics
  def semantics(name: String): Option[(Func, List[Value])] = {
    (info.semMap.get(name + k.toString) match {
      case Some(f) => Some((f, list.map(_._2)))
      case None => info.semMap.get(name + info.maxK.toString).map((f) => (f, fullList.map(_._2)))
    }) match {
      case Some(f) => Some(f)
      case None => (list match {
        case List((_, ASTVal(x))) => x.semantics(name)
        case _ => None
      })
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
}

trait ASTInfo {
  val maxK: Int
  val semMap: Map[String, Func]
}

case class Lexical(kind: String, str: String) extends AST {
  val name: String = kind
  val k: Int = 0
  val parserParams: List[Boolean] = Nil
  val info: ASTInfo = LexicalInfo
  val fullList: List[(String, Value)] = Nil
  override def toString: String = str
}
object LexicalInfo extends ASTInfo {
  val maxK: Int = 0
  val semMap: Map[String, Func] = Map()
}
