package kr.ac.kaist.jiset.spec.algorithm

// method algorithm heads
case class MethodHead(
    base: String,
    methodName: String,
    receiverParam: String,
    origParams: List[String]
) extends Head {
  // name from base and method name
  val name: String = s"$base.$methodName"

  // prepend `this` parameter
  val params: List[Param] = (receiverParam :: origParams).map(Param(_))
}
