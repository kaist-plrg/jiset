object min extends Algorithm {
  val length: Int = 2
  val lang: Boolean = false
  val func: Func = FixUIdWalker(parseFunc(""""min" (a, b) => {
    if (< a b) return a
    else return b
  }"""))
}
