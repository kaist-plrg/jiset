object Type extends Algorithm {
  val name: String = "Type"
  val length: Int = 1
  val lang: Boolean = false
  val func: Func = FixUIdWalker(parseFunc(""""Type" (argument) => {
    val t = (typeof argument)
    if (|| (= t "ArgumentsExoticObject")
      (|| (= t "ArrayExoticObject")
      (|| (= t "BoundFunctionExoticObject")
      (|| (= t "BuiltinFunctionObject")
      (|| (= t "ECMAScriptFunctionObject")
      (|| (= t "ImmutablePrototypeExoticObject")
      (|| (= t "IntegerIndexedExoticObject")
      (|| (= t "ModuleNamespaceExoticObject")
      (|| (= t "OrdinaryObject")
      (|| (= t "ProxyExoticObject")
      (= t "StringExoticObject"))))))))))) return "Object"
    else return t
  }"""), this)
}
