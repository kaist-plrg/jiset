Array.prototype.indexOf = function indexOf(arg1) {
  var result = -1
  var n0 = this.length
  for (var i = 0; i < n0; i += 1) {
    var n1 = this[i]
    if (n1==arg1) {
      result = i
      break
    }
  }
  return result
}
