package kr.ac.kaist.ase.core

// CORE Properties
sealed trait RefValue extends CoreNode
case class RefValueId(id: Id) extends RefValue
case class RefValueProp(addr: Addr, value: Value) extends RefValue
case class RefValueAST(ast: ASTVal, name: String) extends RefValue