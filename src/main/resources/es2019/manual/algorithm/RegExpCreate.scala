object RegExpCreate extends Algorithm {
  val length: Int = 2
  val lang: Boolean = false
  val func: Func = FixUIdWalker(parseFunc(""""RegExpCreate" (pattern, flags) => {
    !!! "RegularExpression"
  }"""))
}
