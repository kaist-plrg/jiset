package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.error.InvalidAST
import kr.ac.kaist.jiset.spec.grammar._
import kr.ac.kaist.jiset.util.{ Span, Pos }
import io.circe._, io.circe.syntax._

object Lexical {
  def apply(data: Json): Lexical = AST(data) match {
    case Some(compressed) => Lexical(compressed)
    case None => ???
  }
  def apply(data: AST.Compressed): Lexical = {
    val AST.LexicalCompressed(str) = data
    Lexical("", str) // TODO handle kind
  }
}

case class Lexical(kind: String, str: String) extends AST {
  def idx: Int = 0
  def k: Int = 0
  def parserParams: List[Boolean] = Nil
  def span: Span = Span()
  def fullList: List[(String, Value)] = Nil
  def maxK: Int = 0

  // name
  override def name: String = kind

  // pretty printer
  override def prettify: Json = Json.arr(
    Json.fromString(kind),
    Json.fromString(str),
  )

  // to JSON format
  override def toJson: Json = Json.fromString(str)

  // conversion to string
  override def toString: String = str
}
