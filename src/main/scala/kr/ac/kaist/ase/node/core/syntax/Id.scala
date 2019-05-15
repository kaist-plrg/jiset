package kr.ac.kaist.ase.node.core

// CORE Identifiers
case class Id(name: String) extends CoreNode {
  override def toString = new StringContext("Id(\"", "\")").s(name)
}
