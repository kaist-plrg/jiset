object IsAbruptCompletion {
  val func: Func = parseFunc(""""IsAbruptCompletion" (x) => {
  return (&& (is-completion x) (! (= x.Type CONST_normal)))
  }""")
}
