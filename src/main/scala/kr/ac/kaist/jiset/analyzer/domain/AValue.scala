package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.js.ast.AST
import kr.ac.kaist.jiset.spec.algorithm.Algo
import kr.ac.kaist.jiset.util.Useful._

// values used in analysis
sealed trait AValue
object AValue {
  // from original concrete values
  def from(value: Value): AValue = value match {
    case addr: Addr => Loc.from(addr)
    case Func(algo) => AFunc(algo)
    case ASTVal(ast) => AAst(ast)
    case simple: SimpleValue => ASimple(simple)
    case _ => error(s"impossible to convert to AValue: ${value.beautified}")
  }
}

// abstract locations for addresses
sealed trait Loc extends AValue
object Loc {
  // from original concrete addresses
  def from(addr: Addr): Loc = addr match {
    case NamedAddr(name) => NamedLoc(name)
    case _ => error(s"impossible to convert to Loc: ${addr.beautified}")
  }
}
case class NamedLoc(name: String) extends Loc
case class AllocSite(k: Int, view: View) extends Loc

// functions
case class AFunc(algo: Algo) extends AValue

// closures
case class AClo(
  params: List[Id],
  locals: Map[Id, AbsValue],
  entry: Entry
) extends AValue

// continuations
case class ACont(
  params: List[Id],
  target: NodePoint[Node]
) extends AValue

// AST values
case class AAst(ast: AST) extends AValue

// simple values
case class ASimple(value: SimpleValue) extends AValue
