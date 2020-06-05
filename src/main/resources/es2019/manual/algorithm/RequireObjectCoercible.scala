object RequireObjectCoercible extends Algorithm {
  val name: String = "RequireObjectCoercible"
  val length: Int = 1
  val lang: Boolean = true
  val func: Func = FixUIdWalker(parseFunc(""""RequireObjectCoercible" (argument) => {
    if (|| (= (typeof argument) "Undefined") (= (typeof argument) "Null")) {
      return (new Completion (
        "Type" -> CONST_throw,
        "Value" -> (new OrdinaryObject(
          "Prototype" -> INTRINSIC_TypeErrorPrototype,
          "ErrorData" -> undefined,
          "SubMap" -> (new SubMap())
        )),
        "Target" -> CONST_empty
      ))
    } else {
      return argument
    }
  }"""), this)
}
