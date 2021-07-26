const Node = require('../Node');

// ForBinding[Yield, Await] :
//    BindingIdentifier[?Yield, ?Await]
//    BindingPattern[?Yield, ?Await]
let ForBinding = (Yield, Await) => (given) => {
  const BindingIdentifier = require('./BindingIdentifier');
  const BindingPattern = require('./BindingPattern');
  const params = [Yield, Await];
  const { id } = given;
  if (id.type == 'Identifier') {
    let x = BindingIdentifier(Yield, Await)(id);
    return new Node('ForBinding', given, 0, [x], params);
  } else if (id.type === 'ArrayPattern' || id.type === 'ObjectPattern') {
    const bindingPattern = BindingPattern(Yield, Await)(id);
    return new Node('ForBinding', given, 1, [bindingPattern], params);
  } else {
    Node.TODO(`${id.type} @ ForBinding`);
  }
}

module.exports = ForBinding;
