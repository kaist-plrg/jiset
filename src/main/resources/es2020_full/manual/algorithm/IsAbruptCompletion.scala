
object IsAbruptCompletion {
  val func: Func = parseFunc(""""IsAbruptCompletion" (x) => {
    return (&& (= (typeof x) "Completion") (! (= x.Type CONST_normal)))
  }""")
}
