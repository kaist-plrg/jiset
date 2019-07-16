package kr.ac.kaist.ase

import kr.ac.kaist.ase.parser.ESParsers
import kr.ac.kaist.ase.error.ModelNotYetGenerated
import kr.ac.kaist.ase.core._
import scala.collection.immutable.{ Set => SSet }

package object model {
  val Parser: ESParsers = throw ModelNotYetGenerated
  object Model {
    val initState: State = throw ModelNotYetGenerated
    val tyMap: Map[String, Map[Value, Value]] = throw ModelNotYetGenerated
  }
  object ESValueParser {
    def parseString(str: String): String = throw ModelNotYetGenerated
    def parseNumber(str: String): Double = throw ModelNotYetGenerated
    def str2num(str: String): Double = throw ModelNotYetGenerated
  }
  trait AST {
    def name: String
    def getNames: SSet[String]
    def semantics(name: String): Option[(Func, List[Value])]
    val parserParams: List[Boolean]
    def subs(name: String): Option[ASTVal]
  }
  trait Script extends AST
  trait StatementListItem extends AST
  trait ModelHelper {
    def flattenStatement(s: Script): List[StatementListItem]
    def mergeStatement(l: List[StatementListItem]): Script
  }
}
