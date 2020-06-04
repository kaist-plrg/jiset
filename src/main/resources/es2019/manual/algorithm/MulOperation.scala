object MulOperation extends Algorithm {
  val length: Int = 3
  val lang: Boolean = true
  val func: Func = parseFunc(""""MulOperation" (op, lnum, rnum) => {
    if (= op "*") {
      return ( * lnum rnum )
    } else if (= op "/") {
      return ( / lnum rnum )
    } else if (= op "%") {
      return ( % lnum rnum )
    } else {
      return undefined
    }
  }""")
}
