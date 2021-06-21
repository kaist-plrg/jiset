const Node = require('../Node');

// LexicalBinding[In, Yield, Await] :
//    BindingIdentifier[?Yield, ?Await] Initializer[?In, ?Yield, ?Await]_opt
//    BindingPattern[?Yield, ?Await] Initializer[?In, ?Yield, ?Await]
let LexicalBinding = (In, Yield, Await) => (given) => {
  const BindingIdentifier = require('./BindingIdentifier');
  const Initializer = require('./Initializer');
  let params = [In, Yield, Await];
  if (given.id.type === 'Identifier') {
    let bindingId = BindingIdentifier(Yield, Await)(given.id);
    let init = null;
    if (given.init) init = Initializer(In, Yield, Await)(given.init);
    return new Node('LexicalBinding', given, 0, [bindingId, init], params);
  } else {
    Node.TODO('BindingPattern @ LexicalBinding');
  }
}

module.exports = LexicalBinding;
