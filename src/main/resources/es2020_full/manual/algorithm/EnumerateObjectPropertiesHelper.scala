
object EnumerateObjectPropertiesHelper {
  val func: Func = parseFunc(""""EnumerateObjectPropertiesHelper" ( O , listAll, listEnum ) => {
    app a = (O.OwnPropertyKeys O)
    let __x0__ = 0i
    while (< __x0__ a.length) {
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
    app proto = (O["GetPrototypeOf"] O)
    if (= null proto) {
      return listEnum
    } else {
      app __x1__ = (EnumerateObjectPropertiesHelper proto listAll listEnum)
      return __x1__
    }
  }""")
}
