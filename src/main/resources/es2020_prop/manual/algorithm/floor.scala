object floor {
  val func: Func = parseFunc(""""floor" (a) => {
    return (- a (% a 1))
  }""")
}
