package kr.ac.kaist.jiset.spec.algorithm

// method algorithm heads
case class MethodHead(
    base: String,
    methodName: String,
    receiverParam: Param,
    origParams: List[Param]
) extends Head {
  // name from base and method name
  val name: String = s"$base.$methodName"

  // prepend `this` parameter
  val params: List[Param] = receiverParam :: origParams

  // check if step is let ~ `this` step in internal method algorithms
  def isLetThisStep(step: String): Boolean = (
    Head.letEnvRecPattern.matches(step) ||
    Head.letObjPattern.matches(step)
  )
}
