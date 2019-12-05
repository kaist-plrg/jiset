object WrapCompletion {
  val func: Func = Func("WrapCompletion", List(Id("val")), None, parseInst(
    s"""if (= (typeof val) "Completion") {
        return val
    } else {
      app completion = (NormalCompletion val)
      return completion
    } """
  ))
}
