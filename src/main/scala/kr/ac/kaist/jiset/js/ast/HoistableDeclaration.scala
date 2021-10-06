package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait HoistableDeclaration extends AST { val kind: String = "HoistableDeclaration" }

object AbsHoistableDeclaration extends HoistableDeclaration with AbsAST

object HoistableDeclaration {
  def apply(data: Json): HoistableDeclaration = AST(data) match {
    case Some(compressed) => HoistableDeclaration(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): HoistableDeclaration = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(FunctionDeclaration(_)).get
        HoistableDeclaration0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(GeneratorDeclaration(_)).get
        HoistableDeclaration1(x0, params, span)
      case 2 =>
        val x0 = subs(0).map(AsyncFunctionDeclaration(_)).get
        HoistableDeclaration2(x0, params, span)
      case 3 =>
        val x0 = subs(0).map(AsyncGeneratorDeclaration(_)).get
        HoistableDeclaration3(x0, params, span)
    }
  }
}

case class HoistableDeclaration0(x0: FunctionDeclaration, parserParams: List[Boolean], span: Span) extends HoistableDeclaration {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("FunctionDeclaration", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class HoistableDeclaration1(x0: GeneratorDeclaration, parserParams: List[Boolean], span: Span) extends HoistableDeclaration {
  x0.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("GeneratorDeclaration", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class HoistableDeclaration2(x0: AsyncFunctionDeclaration, parserParams: List[Boolean], span: Span) extends HoistableDeclaration {
  x0.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("AsyncFunctionDeclaration", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class HoistableDeclaration3(x0: AsyncGeneratorDeclaration, parserParams: List[Boolean], span: Span) extends HoistableDeclaration {
  x0.parent = Some(this)
  def idx: Int = 3
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("AsyncGeneratorDeclaration", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
