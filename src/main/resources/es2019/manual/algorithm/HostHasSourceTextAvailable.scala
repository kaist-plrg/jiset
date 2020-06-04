object HostHasSourceTextAvailable extends Algorithm {
  val length: Int = 1
  val lang: Boolean = false
  val func: Func = parseFunc(""""HostHasSourceTextAvailable" (func) => {
    return (new Completion("Type" -> CONST_normal, "Value" -> true, "Target" -> CONST_empty))
  }""")
}
