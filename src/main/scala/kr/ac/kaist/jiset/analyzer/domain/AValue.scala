package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.js.ast.AST
import kr.ac.kaist.jiset.cfg._

// values used in analysis
sealed trait AValue

// abstract locations for addresses
sealed trait Loc extends AValue
case class NamedLoc(name: String, view: View) extends Loc
case class AllocSite(k: Int, view: View) extends Loc

// functions
case class AFunc(func: Function) extends AValue

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
