
object min {
  val func: Func = parseFunc(""""min" (a, b) => {
    if (< a b) return a
    else return b
  }""")
}
