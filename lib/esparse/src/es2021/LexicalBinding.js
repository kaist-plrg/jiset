const Node = require('../Node');

// LexicalBinding[In, Yield, Await] :
//    BindingIdentifier[?Yield, ?Await] Initializer[?In, ?Yield, ?Await]_opt
//    BindingPattern[?Yield, ?Await] Initializer[?In, ?Yield, ?Await]
let LexicalBinding = (In, Yield, Await) => (given) => {
  const BindingIdentifier = require('./BindingIdentifier');
  const BindingPattern = require('./BindingPattern');
  const Initializer = require('./Initializer');
  const params = [In, Yield, Await];

  if (given.id.type === 'Identifier') {
    let bindingId = BindingIdentifier(Yield, Await)(given.id);
    let init = null;
    if (given.init) init = Initializer(In, Yield, Await)(given.init);
    return new Node('LexicalBinding', given, 0, [bindingId, init], params);
  } else if (given.id.type === 'ArrayPattern' || given.id.type === 'ObjectPattern') {
    const bindingId = BindingPattern(Yield, Await)(given.id);
    const init = Initializer(In, Yield, Await)(given.init);
    return new Node('LexicalBinding', given, 1, [bindingId, init], params);
  } else {
    Node.TODO(`${given.id.type} @ LexicalBinding`);
  }
}

module.exports = LexicalBinding;
