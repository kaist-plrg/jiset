object RegExpCreate extends Algorithm {
  val name: String = "RegExpCreate"
  val length: Int = 2
  val lang: Boolean = false
  val func: Func = FixUIdWalker(parseFunc(""""RegExpCreate" (pattern, flags) => {
    !!! "RegularExpression"
  }"""), this)
}
