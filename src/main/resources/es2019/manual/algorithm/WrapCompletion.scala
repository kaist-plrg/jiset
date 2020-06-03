object WrapCompletion {
  val func: Func = Func("WrapCompletion", List(Id("val")), None, parseInst(
    s"""if (is-completion val) {
        return val
    } else {
      app completion = (NormalCompletion val)
      return completion
    } """
  ))
}
