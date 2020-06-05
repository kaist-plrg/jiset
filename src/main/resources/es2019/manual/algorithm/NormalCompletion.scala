object NormalCompletion extends Algorithm {
  val name: String = "NormalCompletion"
  val length: Int = 1
  val lang: Boolean = false
  val func: Func = FixUIdWalker(parseFunc(""""NormalCompletion" (argument) => {
    return (new Completion(
      "Type" -> CONST_normal,
      "Value" -> argument,
      "Target" -> CONST_empty
    ))
  }"""), this)
}
