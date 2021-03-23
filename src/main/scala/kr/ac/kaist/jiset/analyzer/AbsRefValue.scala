package kr.ac.kaist.jiset.analyzer

// abstract reference
sealed trait AbsRef {
  // conversion to string
  override def toString: String = this match {
    case AbsId(x) => x
    case AbsStrProp(base, str) => s"$base.$str"
    case AbsGeneralProp(base, prop) => s"$base[$prop]"
  }
}
case class AbsId(name: String) extends AbsRef
case class AbsStrProp(base: Type, str: String) extends AbsRef
case class AbsGeneralProp(base: Type, prop: Type) extends AbsRef
