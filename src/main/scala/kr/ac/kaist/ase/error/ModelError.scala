package kr.ac.kaist.ase.error

sealed abstract class ModelError(msg: String) extends ASEError(msg)

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
