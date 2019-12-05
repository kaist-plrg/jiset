
object HostPrint {
  val func: Func = parseFunc(""""HostPrint" (this, argumentsList, NewTarget) => {
    app __x0__ = (GetArgument argumentsList 0i)
    let str = __x0__
    app __x1__ = (ToString str)
    if (= (typeof __x1__) "Completion") if (= __x1__["Type"] CONST_normal) __x1__ = __x1__["Value"] else return __x1__ else {}
    str = __x1__
    if (= REALM.printStr absent) {
      REALM.printStr = str
    } else {
      REALM.printStr = (+ REALM.printStr str)
    }
    return undefined
  }""")
}
