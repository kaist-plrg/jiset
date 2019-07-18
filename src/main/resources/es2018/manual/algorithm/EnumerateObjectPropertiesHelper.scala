package kr.ac.kaist.ase.model

import kr.ac.kaist.ase.core.Parser._
import kr.ac.kaist.ase.core._

object EnumerateObjectPropertiesHelper {
  val func: Func = parseFunc(""""EnumerateObjectPropertiesHelper" ( O , listAll, listEnum ) => {
    let a = (map-keys O["SubMap"])
    let __x0__ = 0i
    while (< __x0__ (length-of a)) {
      let c = a[__x0__]
      if (&& (= (typeof c) "String") (! (contains listAll c))) {
        append c -> listAll
        if (= O["SubMap"][c]["Enumerable"] true)
        {
          append c -> listEnum
        } else {}
      } else {}
      __x0__ = (+ __x0__ 1i)
    }
    let proto = (O["GetPrototypeOf"] O)
    if (= null proto) {
      return listEnum
    } else {
      return (EnumerateObjectPropertiesHelper proto listAll listEnum)
    }
  }""")
}
