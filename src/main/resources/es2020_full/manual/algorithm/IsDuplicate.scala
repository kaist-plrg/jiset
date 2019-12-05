
object IsDuplicate {
  val func: Func = parseFunc(""""IsDuplicate" (list) => {
    let i = 0i
    let len = list.length
    while (< i len) {
      let j = (+ i 1i)
      while (< j len) {
        if (= list[i] list[j]) return true
        else {}
        j = (+ j 1i)
      }
      i = (+ i 1i)
    }
    return false
  }""")
}
