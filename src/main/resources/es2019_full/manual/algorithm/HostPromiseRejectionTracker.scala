object HostPromiseRejectionTracker {
  val func: Func = parseFunc(""""HostPromiseRejectionTracker" (promise, operation) => {
    return (new Completion("Type" -> CONST_normal, "Value" -> undefined, "Target" -> CONST_empty))
  }""")
}
