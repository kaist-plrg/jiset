package kr.ac.kaist.ase.model

import kr.ac.kaist.ase.core.Parser._
import kr.ac.kaist.ase.core._

object Type {
  val func: Func = parseFunc(""""Type" (argument) => {
    val t = (typeof argument)
    if (= t "Completion") t = (typeof argument["Value"]) else {}
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
  }""")
}
