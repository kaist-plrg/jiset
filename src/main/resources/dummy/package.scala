package kr.ac.kaist.ase

import kr.ac.kaist.ase.parser.ASTParsers
import kr.ac.kaist.ase.error.ModelNotYetGenerated
import kr.ac.kaist.ase.core._
import scala.collection.immutable.{ Set => SSet }

package object model {
  val ASTParser: ASTParsers = throw ModelNotYetGenerated
  object Model {
    val initState: State = throw ModelNotYetGenerated
    val tyMap: Map[String, Map[Value, Value]] = Map()
  }
  trait AST {
    def getNames: SSet[String]
    def semantics: Map[String, (Func, List[Value])]
    val parserParams: List[Boolean]
  }
  trait Script extends AST
}
