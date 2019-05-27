package kr.ac.kaist.ase

import kr.ac.kaist.ase.parser.ASTParsers
import kr.ac.kaist.ase.error.ModelNotYetGenerated
import kr.ac.kaist.ase.core._

package object model {
  val ASTParser: ASTParsers = throw ModelNotYetGenerated
  object Model { val initState: State = throw ModelNotYetGenerated }
  trait AST { val semantics: Map[String, (Func, List[Value])] }
  trait Script extends AST
}
