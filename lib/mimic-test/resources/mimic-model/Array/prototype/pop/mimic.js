Array.prototype.pop = function pop() {
  var n0 = this.length
  if (n0) {
    var n1 = this[n0-1]
    this.length = n0-1
    delete this[n0-1]
    return n1
  } else {
    this.length = 0
  }
}
