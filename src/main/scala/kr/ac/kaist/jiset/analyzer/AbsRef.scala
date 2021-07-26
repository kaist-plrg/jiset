package kr.ac.kaist.jiset.analyzer

// abstract reference
sealed trait AbsRef extends Component
case class AbsId(name: String) extends AbsRef
case class AbsStrProp(base: AbsType, str: String) extends AbsRef
case class AbsGeneralProp(base: AbsType, prop: AbsType) extends AbsRef
