const Node = require('../Node');

// ForBinding[Yield, Await] :
//    BindingIdentifier[?Yield, ?Await]
//    BindingPattern[?Yield, ?Await]
let ForBinding = (Yield, Await) => (given) => {
  const BindingIdentifier = require('./BindingIdentifier');
  // const BindingPattern = require('./BindingPattern');
  let params = [Yield, Await];
  let id = (given.id);
  if (id.type == 'Identifier') {
    let x = BindingIdentifier(Yield, Await)(id);
    return new Node('ForBinding', given, 0, [x], params);
  } else {
    Node.TODO(`${id.type} @ ForBinding`);
  }
}

module.exports = ForBinding;
