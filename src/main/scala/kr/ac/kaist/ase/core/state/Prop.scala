package kr.ac.kaist.ase.core

// CORE Properties
sealed trait Prop extends CoreNode
case class GlobalId(id: Id) extends Prop
case class PropId(addr: Addr, id: Id) extends Prop
case class PropStr(addr: Addr, str: String) extends Prop
