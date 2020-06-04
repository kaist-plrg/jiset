object ToNumber extends Algorithm {
  val length: Int = 1
  val lang: Boolean = true
  val func: Func = parseFunc(""""ToNumber" (argument) => {
    app atype = (Type argument)
    if (= atype "Undefined") return NaN
    else if (= atype "Null") return 0
    else if (= atype "Boolean") if argument return 1 else return 0
    else if (= atype "Number") return argument
    else if (= atype "String") return (convert argument str2num)
    else if (= atype "Symbol") return (new Completion (
      "Type" -> CONST_throw,
      "Value" -> (new OrdinaryObject(
        "Prototype" -> INTRINSIC_TypeErrorPrototype,
        "ErrorData" -> undefined,
        "SubMap" -> (new SubMap())
      )),
      "Target" -> CONST_empty
    )) else {
      app __x0__ = (ToPrimitive argument "Number")
      if (is-completion __x0__) {
        if (= __x0__.Type CONST_normal) __x0__ = __x0__.Value
        else return __x0__
      } else {}
      let primValue = __x0__
      app __x1__ = (ToNumber primValue)
      if (is-completion __x1__) {
        if (= __x1__.Type CONST_normal) __x1__ = __x1__.Value
        else return __x1__
      } else {}
      return __x1__
    }
  }""")
}
