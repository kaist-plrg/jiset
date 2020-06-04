object IsArrayIndex extends Algorithm {
  val length: Int = 1
  val lang: Boolean = true
  val func: Func = parseFunc(""""IsArrayIndex" (P) => {
    app u = (ToUint32 P)
    if (= u 4294967295i) return false else {}
    app s = (ToString u)
    return (= s P)
  }""")
}
