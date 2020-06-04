object WrapCompletion extends Algorithm {
  val length: Int = 1
  val lang: Boolean = false
  val func: Func = FixUIdWalker(parseFunc(""""WrapCompletion" (val) => {
    if (is-completion val) {
      return val
    } else {
      app completion = (NormalCompletion val)
      return completion
    }
  }"""))
}
