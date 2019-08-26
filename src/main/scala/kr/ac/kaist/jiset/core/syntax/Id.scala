package kr.ac.kaist.jiset.core

// CORE Identifiers
case class Id(name: String) extends CoreNode {
  override def toString = s"""Id("$name")"""
}
