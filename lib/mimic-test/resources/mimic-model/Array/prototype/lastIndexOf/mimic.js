Array.prototype.lastIndexOf = function lastIndexOf(arg1) {
  var n0 = this.length
  for (var i = 0; i < n0; i += 1) {
    var n1 = this[(n0-i)-1]
    if (n1==arg1) {
      break
    }
  }
  return (n0-i)-1
}
