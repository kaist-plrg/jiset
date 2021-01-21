package kr.ac.kaist.jiset.spec.algorithm

import kr.ac.kaist.jiset.spec.{ ECMAScript, Region }
import kr.ac.kaist.jiset.spec.grammar._
import kr.ac.kaist.jiset.util.Conversion._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util.{ InfNum, PInf }
import org.jsoup.nodes._
import scala.util.matching.Regex._

trait Head {
  // name
  val name: String

  // parameters
  val params: List[Param]

  // section id
  val secId: String

  // conversion to string
  override def toString: String = s"$name (${params.mkString(", ")}):"

  // arity
  lazy val arity: (InfNum, InfNum) =
    params.foldLeft[(InfNum, InfNum)]((0, 0)) {
      case ((s, e), p) => {
        val (ps, pe) = p.count
        (s + ps, e + pe)
      }
    }
}
object Head {
  // get names and parameters
  val paramPattern = "[^\\s,()\\[\\]]+".r
  val rulePattern = ".*(Statement|Expression)\\s*Rules".r
  val namePattern = "[.:a-zA-Z0-9%\\[\\]@ /`_-]+".r
  val prefixPattern = ".*Semantics:".r
  val withParamPattern = "_\\w+_".r
  val normPattern = "[\\s|-]".r
  val thisValuePattern = "this\\w+Value".r
  val letEnvRecPattern = "1. Let ([_\\w]+) be the \\w+ Environment Record.*".r
  val letObjPattern = "1. Let ([_\\w]+) be the \\w+ object\\.".r
  val methodDescPattern = """(?:The|When the)[\s\w\[\]]+method (?:of|for)[\s\w]+,? (_\w+_),? (?:takes (?:no )?arguments?|is called|that was created|which was created).*""".r
}
