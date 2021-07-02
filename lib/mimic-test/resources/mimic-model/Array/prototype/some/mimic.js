Array.prototype.some = function some(arg1, arg2) {
  var n0 = this.length
  var n3 = false
  for (var i = 0; i < n0; i += 1) {
    var n1 = i in this
    if (n1) {
      var n2 = this[i]
      n3 = arg1.call(arg2, n2, i, this)
      if (n3) {
        break
      }
    }
  }
  return n3
}
