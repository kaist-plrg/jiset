package kr.ac.kaist.jiset.checker

// abstract reference
sealed trait AbsRef extends CheckerComponent
case class AbsId(name: String) extends AbsRef
case class AbsStrProp(base: AbsType, str: String) extends AbsRef
case class AbsGeneralProp(base: AbsType, prop: AbsType) extends AbsRef
