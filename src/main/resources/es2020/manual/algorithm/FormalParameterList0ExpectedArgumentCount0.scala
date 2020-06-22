object FormalParameterList0ExpectedArgumentCount0 extends Algorithm {
  val name: String = "FormalParameterList0ExpectedArgumentCount0"
  val length: Int = 0
  val lang: Boolean = true
  val func: Func = FixUIdWalker(parseFunc(""""FormalParameterList0ExpectedArgumentCount0" (this, FormalParameter) => {
    access __x0__ = (FormalParameter "HasInitializer")
    if (= __x0__ true) return 0
    else {}
    return 1
  }"""), this)
}
