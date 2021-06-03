package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.TRIPLE

// IR Identifiers
case class Id(name: String) extends IRNode {
  override def toString = s"Id($TRIPLE$name$TRIPLE)"
}
