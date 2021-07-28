package kr.ac.kaist.jiset.ir

// Walker for complete check (not containing ???) of instructions
class CompleteChecker extends UnitWalker {
  var complete = true
  override def walk(expr: Expr): Unit = expr match {
    case ENotSupported(_) => complete = false
    case _ => super.walk(expr)
  }
}
