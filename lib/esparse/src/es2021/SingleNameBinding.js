const Node = require('../Node');

// SingleNameBinding[Yield, Await] :
//    BindingIdentifier[?Yield, ?Await] Initializer[+In, ?Yield, ?Await]opt
let SingleNameBinding = (Yield, Await) => (given) => {
  const BindingIdentifier = require('./BindingIdentifier');
  const Initializer = require('./Initializer');
  let params = [Yield, Await];
  let { type, left, right } = given;
  if (type == 'Identifier') {
    let x = BindingIdentifier(Yield, Await)(given);
    return new Node('SingleNameBinding', given, 0, [x, null], params);
  } else {
    let x = BindingIdentifier(Yield, Await)(left);
    let init = Initializer(true, Yield, Await)(right);
    return new Node('SingleNameBinding', given, 0, [x, init], params);
  }
}

module.exports = SingleNameBinding;
