Array.prototype.shift = function shift() {
  var n0 = this.length
  if (n0) {
    var n1 = this[0]
    for (var i = 0; i < (n0-1); i += 1) {
      var n2 = (i+1) in this
      if (n2) {
        var n3 = this[i+1]
        this[i] = n3
      } else {
        delete this[i]
      }
    }
    delete this[i]
    this.length = i
    return n1
  } else {
    this.length = 0
  }
}
