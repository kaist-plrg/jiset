package kr.ac.kaist.ase

import kr.ac.kaist.ase.parser.ESParsers
import kr.ac.kaist.ase.error.ModelNotYetGenerated
import kr.ac.kaist.ase.core._
import scala.collection.immutable.{ Set => SSet }

package object model {
  val Parser: ESParsers = throw ModelNotYetGenerated
  object Model {
    val initState: State = throw ModelNotYetGenerated
    val tyMap: Map[String, Map[Value, Value]] = Map()
  }
  trait AST {
    def name: String
    def getNames: SSet[String]
    def semantics(name: String): Option[(Func, List[Value])]
    val parserParams: List[Boolean]
    def subs(name: String): Option[ASTVal]
  }
  trait Script extends AST
}
