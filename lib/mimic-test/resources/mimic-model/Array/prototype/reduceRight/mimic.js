Array.prototype.reduceRight = function reduceRight(arg1, arg2) {
  var result = arg2
  var n0 = this.length
  for (var i = 0; i < n0; i += 1) {
    var n1 = ((n0-i)-1) in this
    if (n1) {
      var n2 = this[(n0-i)-1]
      var n3 = arg1.call(undefined, result, n2, (n0-i)-1, this)
      result = n3
    }
  }
  return result
}
