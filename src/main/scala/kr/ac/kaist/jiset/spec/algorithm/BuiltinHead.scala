package kr.ac.kaist.jiset.spec.algorithm

// built-in algorithm heads
case class BuiltinHead(
    base: String,
    fields: List[Field],
    origParams: List[String]
) extends AlgoHead {
  // name from base and fields
  val name: String = base + (fields.map(_.toAccessString).mkString)

  // fixed parameters for built-in algorithms
  val params: List[String] = AlgoHead.BUILTIN_PARAMS
}

// fields for built-in algorithm heads
trait Field {
  // conversion to string
  override def toString: String = this match {
    case NormalField(name) => s"$name"
    case SymbolField(name) => s"@@$name"
  }

  // to access string
  def toAccessString: String = this match {
    case NormalField(name) => s".$name"
    case SymbolField(name) => s"[ @@$name ]"
  }
}
case class NormalField(name: String) extends Field
case class SymbolField(name: String) extends Field
