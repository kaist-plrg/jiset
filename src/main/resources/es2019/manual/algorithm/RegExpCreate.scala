object RegExpCreate extends Algorithm {
  val length: Int = 2
  val lang: Boolean = false
  val func: Func = parseFunc(""""RegExpCreate" (pattern, flags) => {
    !!! "RegularExpression"
  }""")
}
