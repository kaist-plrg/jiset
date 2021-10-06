package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait AsyncGeneratorDeclaration extends AST { val kind: String = "AsyncGeneratorDeclaration" }

object AbsAsyncGeneratorDeclaration extends AsyncGeneratorDeclaration with AbsAST

object AsyncGeneratorDeclaration {
  def apply(data: Json): AsyncGeneratorDeclaration = AST(data) match {
    case Some(compressed) => AsyncGeneratorDeclaration(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): AsyncGeneratorDeclaration = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(BindingIdentifier(_)).get
        val x1 = subs(1).map(FormalParameters(_)).get
        val x2 = subs(2).map(AsyncGeneratorBody(_)).get
        AsyncGeneratorDeclaration0(x0, x1, x2, params, span)
      case 1 =>
        val x0 = subs(0).map(FormalParameters(_)).get
        val x1 = subs(1).map(AsyncGeneratorBody(_)).get
        AsyncGeneratorDeclaration1(x0, x1, params, span)
    }
  }
}

case class AsyncGeneratorDeclaration0(x4: BindingIdentifier, x6: FormalParameters, x9: AsyncGeneratorBody, parserParams: List[Boolean], span: Span) extends AsyncGeneratorDeclaration {
  x4.parent = Some(this)
  x6.parent = Some(this)
  x9.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x9, d(x6, d(x4, 0)))
  def fullList: List[(String, PureValue)] = l("AsyncGeneratorBody", x9, l("FormalParameters", x6, l("BindingIdentifier", x4, Nil))).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"async function * $x4 ( $x6 ) { $x9 }"
  }
}

case class AsyncGeneratorDeclaration1(x5: FormalParameters, x8: AsyncGeneratorBody, parserParams: List[Boolean], span: Span) extends AsyncGeneratorDeclaration {
  x5.parent = Some(this)
  x8.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x8, d(x5, 0))
  def fullList: List[(String, PureValue)] = l("AsyncGeneratorBody", x8, l("FormalParameters", x5, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"async function * ( $x5 ) { $x8 }"
  }
}
