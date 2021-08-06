package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.js.ast.AST
import kr.ac.kaist.jiset.spec.algorithm.Algo
import kr.ac.kaist.jiset.util.Useful._

// values used in analysis
sealed trait AValue {
  // conversion to string
  override def toString: String = this match {
    case AComp(AConst("noraml"), value, _) => s"N($value)"
    case AComp(ty, value, target) => s"C($ty, $value, $target)"
    case AConst(name) => s"~$name~"
    case NamedLoc(name) => s"#$name"
    case AllocSite(k, view) => s"#$k:$view"
    case SubMapLoc(baseLoc) => s"$baseLoc:SubMap"
    case AFunc(algo) => s"λ(${algo.name})"
    case AClo(params, locals, entry) => (
      params.mkString("(", ", ", ")") +
      (for ((x, v) <- locals) yield s"$x -> $v").mkString("[", ", ", "]") +
      s" => ${entry.uidString}"
    )
    case ACont(pararms, target) =>
      s"${pararms.mkString("(", ", ", ")")} [=>] $target"
    case AAst(ast) =>
      val max = AValue.AST_MAX_LENGTH
      var str = ast.toString
      if (str.length > max) str = str.substring(0, max - 3) + "..."
      s"☊[${ast.kind}]($str)"
    case ASimple(simple) => simple.toString
  }
}
object AValue {
  val AST_MAX_LENGTH = 10

  // from original concrete values
  def from(value: Value): AValue = value match {
    case Const(name) => AConst(name)
    case addr: Addr => Loc.from(addr)
    case Func(algo) => AFunc(algo)
    case ASTVal(ast) => AAst(ast)
    case simple: SimpleValue => ASimple(simple)
    case _ => error(s"impossible to convert to AValue: $value")
  }
}

// completions
case class AComp(ty: AConst, value: AValue, target: AValue) extends AValue

// constants
case class AConst(name: String) extends AValue

// abstract locations for addresses
sealed trait Loc extends AValue
object Loc {
  // from original concrete addresses
  private val subMapPattern = "(.+).SubMap".r
  def from(addr: Addr): Loc = addr match {
    case NamedAddr(name) => name match {
      case subMapPattern(base) => SubMapLoc(NamedLoc(base))
      case name => NamedLoc(name)
    }
    case _ => error(s"impossible to convert to Loc: $addr")
  }
}
sealed trait BaseLoc extends Loc
case class NamedLoc(name: String) extends BaseLoc
case class AllocSite(k: Int, view: View) extends BaseLoc
case class SubMapLoc(baseLoc: BaseLoc) extends Loc

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
