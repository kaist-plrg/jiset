object ThrowCompletion extends Algorithm {
  val name: String = "ThrowCompletion"
  val length: Int = 1
  val lang: Boolean = false
  val func: Func = FixUIdWalker(parseFunc(""""ThrowCompletion" (argument) => {
    return (new Completion(
      "Type" -> CONST_throw,
      "Value" -> argument,
      "Target" -> CONST_empty
    ))
  }"""), this)
}
