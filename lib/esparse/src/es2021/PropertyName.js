const Node = require('../Node');

// PropertyName[Yield, Await] :
//    LiteralPropertyName
//    ComputedPropertyName[?Yield, ?Await]

let PropertyName = (Yield, Await) => (given) => {
  let LiteralPropertyName = require('./LiteralPropertyName');
  let ComputedPropertyName = require('./ComputedPropertyName');
  
  let params = [Yield, Await];
  let { computed, key } = given;
  if (!computed) {
    let name = LiteralPropertyName(key);
    return new Node('PropertyName', given, 0, [name], params);
  } else {
    let name = ComputedPropertyName(Yield, Await)(key);
    return new Node('PropertyName', given, 1, [name], params);
  }
}

module.exports = PropertyName;
