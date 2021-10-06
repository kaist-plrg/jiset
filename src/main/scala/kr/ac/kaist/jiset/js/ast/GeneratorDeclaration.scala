package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait GeneratorDeclaration extends AST { val kind: String = "GeneratorDeclaration" }

object AbsGeneratorDeclaration extends GeneratorDeclaration with AbsAST

object GeneratorDeclaration {
  def apply(data: Json): GeneratorDeclaration = AST(data) match {
    case Some(compressed) => GeneratorDeclaration(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): GeneratorDeclaration = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(BindingIdentifier(_)).get
        val x1 = subs(1).map(FormalParameters(_)).get
        val x2 = subs(2).map(GeneratorBody(_)).get
        GeneratorDeclaration0(x0, x1, x2, params, span)
      case 1 =>
        val x0 = subs(0).map(FormalParameters(_)).get
        val x1 = subs(1).map(GeneratorBody(_)).get
        GeneratorDeclaration1(x0, x1, params, span)
    }
  }
}

case class GeneratorDeclaration0(x2: BindingIdentifier, x4: FormalParameters, x7: GeneratorBody, parserParams: List[Boolean], span: Span) extends GeneratorDeclaration {
  x2.parent = Some(this)
  x4.parent = Some(this)
  x7.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x7, d(x4, d(x2, 0)))
  def fullList: List[(String, PureValue)] = l("GeneratorBody", x7, l("FormalParameters", x4, l("BindingIdentifier", x2, Nil))).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"function * $x2 ( $x4 ) { $x7 }"
  }
}

case class GeneratorDeclaration1(x3: FormalParameters, x6: GeneratorBody, parserParams: List[Boolean], span: Span) extends GeneratorDeclaration {
  x3.parent = Some(this)
  x6.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x6, d(x3, 0))
  def fullList: List[(String, PureValue)] = l("GeneratorBody", x6, l("FormalParameters", x3, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"function * ( $x3 ) { $x6 }"
  }
}
