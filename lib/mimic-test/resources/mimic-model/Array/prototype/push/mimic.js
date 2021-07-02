Array.prototype.push = function push() {
  var n0 = this.length
  for (var i = 0; i < (arguments.length-1); i += 1) {
    this[n0+i] = arguments[i+1]
  }
  this.length = n0 + i
  var n1 = this.length;
  return n1;
}
