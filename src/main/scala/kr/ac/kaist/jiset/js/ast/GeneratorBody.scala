package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait GeneratorBody extends AST { val kind: String = "GeneratorBody" }

case class GeneratorBody0(x0: FunctionBody, parserParams: List[Boolean], span: Span) extends GeneratorBody {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("FunctionBody", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
