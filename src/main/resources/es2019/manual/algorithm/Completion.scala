object Completion extends Algorithm {
  val name: String = "Completion"
  val length: Int = 0
  val lang: Boolean = true
  val func: Func = FixUIdWalker(parseFunc(""""Completion" (argument) => {
    return argument
  }"""), this)
}
