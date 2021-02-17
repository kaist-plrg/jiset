package kr.ac.kaist.jiset.ir

// IR Identifiers
case class Id(name: String) extends IRNode {
  override def toString = s"Id($TRIPLE$name$TRIPLE)"
}
