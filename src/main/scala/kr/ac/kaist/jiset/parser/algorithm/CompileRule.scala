package kr.ac.kaist.jiset.parser.algorithm

import kr.ac.kaist.jiset.util.UId
import scala.util.parsing.combinator._

trait CompileRuleTree
case class CRLeaf(uid: Int) extends CompileRuleTree
case class CRBranch(op: CROp, left: CompileRuleTree, right: CompileRuleTree) extends CompileRuleTree

// TODO enumeration
trait CROp

case class CompileRule[+T](parser: Parsers#Parser[T], tree: CompileRuleTree) extends UId {
  def |[U >: T](rule: CompileRule[U]): CompileRule[U] = ???
  def |||[U >: T](rule: CompileRule[U]): CompileRule[U] = ???
  def ~[U >: T](rule: CompileRule[U]): CompileRule[U] = ???
  def ~>[U >: T](rule: CompileRule[U]): CompileRule[U] = ???
  def <~[U >: T](rule: CompileRule[U]): CompileRule[U] = ???
  def opt[U >: T](rule: CompileRule[U]): CompileRule[U] = ???
  def rep1sep[U >: T](rule: CompileRule[U]): CompileRule[U] = ???
  def rep[U >: T](rule: CompileRule[U]): CompileRule[U] = ???
  def guard[U >: T](rule: CompileRule[U]): CompileRule[U] = ???
  def ^^[U](f: T => U): CompileRule[U] = ???
  def ^^^[U](f: T => U): CompileRule[U] = ???
}
object ImplicitConversions {
  implicit def parser2rule[T](p: Parsers#Parser[T]): CompileRule[T] = ???
}
