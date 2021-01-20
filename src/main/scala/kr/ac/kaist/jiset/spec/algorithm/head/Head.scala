package kr.ac.kaist.jiset.spec.algorithm

import kr.ac.kaist.jiset.parser.grammar.ProductionParser
import kr.ac.kaist.jiset.spec.{ ECMAScript, Region }
import kr.ac.kaist.jiset.spec.grammar._
import kr.ac.kaist.jiset.util.Useful._
import org.jsoup.nodes._
import scala.util.matching.Regex._

trait Head {
  // name
  val name: String

  // parameters
  val params: List[Param]

  // conversion to string
  override def toString: String = s"$name (${params.mkString(", ")}):"
}
