package kr.ac.kaist.jiset.ir

// IR Properties
sealed trait RefValue extends IRNode
case class RefValueId(id: Id) extends RefValue
case class RefValueProp(addr: Addr, value: Value) extends RefValue
case class RefValueString(str: String, name: Value) extends RefValue
