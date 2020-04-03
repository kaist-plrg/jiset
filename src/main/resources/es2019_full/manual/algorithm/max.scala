object max {
  val func: Func = parseFunc(""""max" (...args) => {
    let x = 1i
    let len = args.length
    let res = args[0i]
    while (< x len) {
      let v = args[x]
      if (< res v) res = v
      else {}
      x = (+ x 1i)
    }
    return res
  }""")
}