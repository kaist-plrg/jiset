object floor extends Algorithm {
  val length: Int = 1
  val lang: Boolean = false
  val func: Func = FixUIdWalker(parseFunc(""""floor" (a) => {
    return (- a (% a 1))
  }"""))
}
