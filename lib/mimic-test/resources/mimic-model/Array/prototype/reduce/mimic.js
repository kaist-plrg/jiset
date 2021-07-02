Array.prototype.reduce = function reduce(arg1, arg2) {
  var result = arg2
  var n0 = this.length
  for (var i = 0; i < n0; i += 1) {
    var n1 = i in this
    if (n1) {
      var n2 = this[i]
      var n3 = arg1.call(undefined, result, n2, i, this)
      result = n3
    }
  }
  return result
}
