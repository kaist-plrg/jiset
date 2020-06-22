object HostReportErrors extends Algorithm {
  val name: String = "HostReportErrors"
  val length: Int = 0
  val lang: Boolean = false
  val func: Func = FixUIdWalker(parseFunc(""""HostReportErrors" (errorList) => {
    if (< 0 errorList.length) {
      if (= undefined errorList[0i].ErrorData) {
        if (= absent errorList[0i].Prototype) {} else {
          if (= absent errorList[0i].Prototype.SubMap) {} else {
            if (= absent errorList[0i].Prototype.SubMap.name) {} else {
              assert errorList[0i].Prototype.SubMap.name.Value
            }
          }
        }
      } else {
        assert errorList[0i].ErrorData
      }
    } else {}
    assert "Error occured"
    return (new Completion("Type" -> CONST_normal, "Value" -> undefined, "Target" -> CONST_empty))
  }"""), this)
}
