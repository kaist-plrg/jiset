object MulOperation {
  val func: Func = Func("MulOperation", List(Id("op"), Id("lnum"), Id("rnum")), None, parseInst(
    s"""if (= op "*") {
          return ( * lnum rnum )
        } else if (= op "/") {
          return ( / lnum rnum )
        } else if (= op "%") {
          return ( % lnum rnum )
        } else {
          return undefined
        }"""
  ))
}
