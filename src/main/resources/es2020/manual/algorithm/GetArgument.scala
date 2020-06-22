object GetArgument extends Algorithm {
  val name: String = "GetArgument"
  val length: Int = 0
  val lang: Boolean = false
  val func: Func = FixUIdWalker(parseFunc(""""GetArgument" (argumentsList, idx) => {
    let arg = argumentsList[idx]
    if (= arg absent) return undefined
    else return arg
  }"""), this)
}
