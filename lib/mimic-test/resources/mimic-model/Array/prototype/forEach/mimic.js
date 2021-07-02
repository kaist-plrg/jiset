Array.prototype.forEach = function forEach(arg1, arg2) {
  var n0 = this.length
  for (var i = 0; i < n0; i += 1) {
    var n1 = i in this
    if (n1) {
      var n2 = this[i]
      var n3 = arg1.call(arg2, n2, i, this)
    }
  }
}
