const Node = require('../Node');

// BindingRestElement[Yield, Await] :
//    ... BindingIdentifier[?Yield, ?Await]
//    ... BindingPattern[?Yield, ?Await]
let BindingRestElement = (Yield, Await) => (given) => {
  const BindingIdentifier = require('./BindingIdentifier');
  let params = [Yield, Await];
  let { argument } = given;
  let { type } = argument;
  if (type == 'Identifier') {
    let id = BindingIdentifier(Yield, Await)(argument);
    return new Node('BindingRestElement', given, 0, [id], params);
  } else {
    let pat = BindingPattern(Yield, Await)(argument);
    return new Node('BindingRestElement', given, 1, [pat], params);
  }
}

module.exports = BindingRestElement;
