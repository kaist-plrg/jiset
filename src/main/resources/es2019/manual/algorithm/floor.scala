object floor extends Algorithm {
  val length: Int = 1
  val lang: Boolean = false
  val func: Func = parseFunc(""""floor" (a) => {
    return (- a (% a 1))
  }""")
}
