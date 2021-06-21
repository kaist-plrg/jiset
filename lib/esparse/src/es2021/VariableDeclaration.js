const Node = require('../Node');

// VariableDeclaration[In, Yield, Await] :
//    BindingIdentifier[?Yield, ?Await] Initializer[?In, ?Yield, ?Await]_opt
//    BindingPattern[?Yield, ?Await]
let VariableDeclaration = (In, Yield, Await) => (given) => {
  let BindingIdentifier = require('./BindingIdentifier');
  let Initializer = require('./Initializer');

  let params = [In, Yield, Await];
  if (given.id.type === 'Identifier') {
    let bindingId = BindingIdentifier(Yield, Await)(given.id);
    let init = null;
    if (given.init) init = Initializer(In, Yield, Await)(given.init);
    return new Node('VariableDeclaration', given, 0, [bindingId, init], params);
  } else {
    Node.TODO('BindingPattern @ VariableDeclaration');
  }
}

module.exports = VariableDeclaration;
