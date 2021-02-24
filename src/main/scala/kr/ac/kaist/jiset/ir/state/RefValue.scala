package kr.ac.kaist.jiset.ir.state

// reference values
sealed trait RefValue
case class RefValueId(id: String) extends RefValue
case class RefValueProp(addr: Addr, prop: String) extends RefValue
case class RefValueString(str: Str, name: String) extends RefValue
