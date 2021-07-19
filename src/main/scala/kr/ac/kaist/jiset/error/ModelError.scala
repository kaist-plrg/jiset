package kr.ac.kaist.jiset.error

sealed abstract class ModelError(msg: String) extends JISETError(msg)

case object ModelNotYetGenerated extends ModelError({
  s"Models are not yet generated. Please generate models using 'sbt generateModel'."
})

case class UnexpectedSemantics(name: String) extends ModelError({
  s"unexpected semantics: $name"
})

case class WrongNumberOfParserParams(name: String, list: List[Boolean]) extends ModelError({
  s"wrong number of parameters for $name: $list"
})

case class NotSupported(msg: String) extends ModelError({
  s"[NotSupported]: $msg"
})

case object InterpTimeout extends ModelError({
  s"timeout during the conrete interpretation."
})

case object InvalidAST extends ModelError({
  s"invald abstract syntax tree"
})

case class WrongUId(uid: Int) extends ModelError({
  s"[WrongUId] uid $uid does not exist."
})
