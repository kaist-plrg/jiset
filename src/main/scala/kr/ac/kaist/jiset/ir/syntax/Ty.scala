package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.TRIPLE

// IR Types
case class Ty(name: String) extends IRNode {
  override def toString: String = s"Ty($TRIPLE$name$TRIPLE)"
}
