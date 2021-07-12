package kr.ac.kaist.jiset.spec.algorithm

import kr.ac.kaist.jiset.TRIPLE
import kr.ac.kaist.jiset.ir
import kr.ac.kaist.jiset.spec.{ Parser, SpecComponent }
import kr.ac.kaist.jiset.spec.algorithm.Param.Kind._
import kr.ac.kaist.jiset.spec.grammar._
import kr.ac.kaist.jiset.spec.grammar.token.NonTerminal
import kr.ac.kaist.jiset.spec.{ ECMAScript, Region }
import kr.ac.kaist.jiset.util.Useful._
import org.jsoup.nodes._

trait Head extends SpecComponent {
  // name
  def name: String = this match {
    case NormalHead(name, _) =>
      name
    case MethodHead(base, methodName, _, _) =>
      s"$base.$methodName"
    case SyntaxDirectedHead(lhsName, idx, subIdx, _, methodName, _) =>
      s"$lhsName[$idx,$subIdx].$methodName"
    case BuiltinHead(ref, origParams) =>
      s"GLOBAL.${ref.beautified}"
  }

  // parameters
  def params: List[Param] = this match {
    case NormalHead(_, params) =>
      params
    case MethodHead(_, _, receiverParam, origParams) =>
      receiverParam :: origParams
    case (head: SyntaxDirectedHead) => {
      val (types, optional) = getInfo(head)
      types.map {
        case (name, _) =>
          val kind = if (optional contains name) Optional else Normal
          Param(name, kind)
      } ++ head.withParams
    }
    case BuiltinHead(ref, origParams) =>
      List(THIS_PARAM, ARGS_LIST, NEW_TARGET).map(Param(_))
  }

  // parameter type names and optional information for syntax-directed algorithms
  private def getInfo(head: SyntaxDirectedHead) = {
    val names = head.rhsNames
    val duplicated = names.filter(p => names.count(_ == p) > 1).toSet
    var counter = Map[String, Int]()
    var optional = Set[String]()
    val types = (THIS_PARAM, head.lhsName) :: (head.rhs.getNTs.map {
      case NonTerminal(name, _, opt) =>
        val newName = if (duplicated contains name) {
          val k = counter.getOrElse(name, 0)
          counter += name -> (k + 1)
          s"$name$k"
        } else name
        if (opt) optional += newName
        (newName, name)
    })
    (types, optional)
  }
}
object Head extends Parser[Head] {
  // get names and parameters
  val paramPattern = "[^\\s,()\\[\\]]+".r
  val rulePattern = ".*(Statement|Expression)\\s*Rules".r
  val namePattern = "[.:a-zA-Z0-9%\\[\\]@ /`_-]+".r
  val prefixPattern = ".*Semantics:".r
  val withParamPattern = "_\\w+_".r
  val optionalParamPattern = "optional parameter(s?).*".r
  val restParamPattern = "rest parameter(s?).*".r
  val normPattern = "[\\s|-]".r
  val thisValuePattern = "this\\w+Value".r
  val letEnvRecPattern = "1. Let ([_\\w]+) be the \\w+ Environment Record.*".r
  val letObjPattern = "1. Let ([_\\w]+) be the \\w+ object\\.".r
  val methodDescPattern = """(?:The|When the)[\s\w\[\]]+method (?:of|for)[\s\w-]+,? (_\w+_),? (?:takes (?:no )?arguments?|is called|that was created|which was created).*""".r
}

// normal algorithm heads
case class NormalHead(
  override val name: String,
  override val params: List[Param]
) extends Head

// method algorithm heads
case class MethodHead(
  base: String,
  methodName: String,
  receiverParam: Param,
  origParams: List[Param]
) extends Head {
  // check if step is let ~ `this` step in internal method algorithms
  def isLetThisStep(code: Iterable[String]): Boolean = code.headOption match {
    case Some(head) =>
      val step = head.trim
      Head.letEnvRecPattern.matches(step) ||
        Head.letObjPattern.matches(step)
    case _ => false
  }
}

// syntax-directed algorithm heads
case class SyntaxDirectedHead(
  lhsName: String,
  idx: Int,
  subIdx: Int,
  rhs: Rhs,
  methodName: String,
  withParams: List[Param]
) extends Head {
  // get rhs names
  def rhsNames: List[String] = rhs.getNTs.map(_.name)
}

// built-in algorithm heads
case class BuiltinHead(
  ref: ir.Ref,
  origParams: List[Param]
) extends Head
