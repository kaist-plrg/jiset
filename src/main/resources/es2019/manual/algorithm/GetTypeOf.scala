object GetTypeOf extends Algorithm {
  val name: String = "GetTypeOf"
  val length: Int = 0
  val lang: Boolean = false
  val func: Func = FixUIdWalker(parseFunc(""""GetTypeOf" (argument) => {
    let t = (typeof argument)
    if (= t "Undefined") return "undefined"
    else if (= t "Null") return "object"
    else if (= t "Boolean") return "boolean"
    else if (= t "Number") return "number"
    else if (= t "String") return "string"
    else if (= t "Symbol") return "symbol"
    else {
      if (= argument.Call absent) return "object"
      else return "function"
    }
  }"""), this)
}
