object HostReportErrors {
  val func: Func = parseFunc(""""HostReportErrors" (errorList) => {
    assert "Error occured"
    return (new Completion("Type" -> CONST_normal, "Value" -> undefined, "Target" -> CONST_empty))
  }""")
}
