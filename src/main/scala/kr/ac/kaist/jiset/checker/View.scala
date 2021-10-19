package kr.ac.kaist.jiset.checker

// view abstraction
case class View(tys: List[Type]) extends CheckerElem
object View {
  def apply(seq: Type*): View =
    if (VIEW) new View(seq.toList) else new View(Nil)
  def apply(tys: List[Type]): View =
    if (VIEW) new View(tys) else new View(Nil)
}
