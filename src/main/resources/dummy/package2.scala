package kr.ac.kaist.jiset

import kr.ac.kaist.jiset.parser.ESParsers
import kr.ac.kaist.jiset.error.ModelNotYetGenerated
import kr.ac.kaist.jiset.core._
import scala.collection.immutable.{ Set => SSet }

package object model {
  val Parser: ESParsers = throw ModelNotYetGenerated
  object Model {
    val initState: State = throw ModelNotYetGenerated
    val tyMap: Map[String, Map[Value, Value]] = throw ModelNotYetGenerated
  }
  object ESValueParser {
    def parseIdentifier(str: String): String = throw ModelNotYetGenerated
    def parseString(str: String): String = throw ModelNotYetGenerated
    def parseNumber(str: String): Double = throw ModelNotYetGenerated
    def parseTVNoSubstitutionTemplate(str: String): String = throw ModelNotYetGenerated
    def parseTRVNoSubstitutionTemplate(str: String): String = throw ModelNotYetGenerated
    def parseTVTemplateHead(str: String): String = throw ModelNotYetGenerated
    def parseTRVTemplateHead(str: String): String = throw ModelNotYetGenerated
    def parseTVTemplateMiddle(str: String): String = throw ModelNotYetGenerated
    def parseTRVTemplateMiddle(str: String): String = throw ModelNotYetGenerated
    def parseTVTemplateTail(str: String): String = throw ModelNotYetGenerated
    def parseTRVTemplateTail(str: String): String = throw ModelNotYetGenerated
    def str2num(str: String): Double = throw ModelNotYetGenerated
  }
  trait Script extends AST
  trait StatementListItem extends AST
  object ModelHelper {
    def checkSupported(ast: AST): Unit = throw ModelNotYetGenerated
    def flattenStatement(s: Script): List[StatementListItem] = throw ModelNotYetGenerated
    def mergeStatement(l: List[StatementListItem]): Script = throw ModelNotYetGenerated
  }
  object NoParse {
    val long: List[String] = throw ModelNotYetGenerated
    val failed: List[String] = throw ModelNotYetGenerated
  }

}
