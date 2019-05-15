package kr.ac.kaist.ase

import kr.ac.kaist.ase.parser.ASTParsers
import kr.ac.kaist.ase.error.ModelNotYetGenerated

package object model {
  val ASTParser: ASTParsers = throw ModelNotYetGenerated
  trait Script
}
