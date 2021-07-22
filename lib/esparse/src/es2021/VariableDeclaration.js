const Node = require('../Node');

// VariableDeclaration[In, Yield, Await] :
//    BindingIdentifier[?Yield, ?Await] Initializer[?In, ?Yield, ?Await]_opt
//    BindingPattern[?Yield, ?Await] Initializer[?In, ?Yield, ?Await]
let VariableDeclaration = (In, Yield, Await) => (given) => {
  let BindingIdentifier = require('./BindingIdentifier');
  let Initializer = require('./Initializer');
  let BindingPattern = require('./BindingPattern');

  let params = [In, Yield, Await];
  if (given.id.type === 'Identifier') {
    let bindingId = BindingIdentifier(Yield, Await)(given.id);
    let init = null;
    if (given.init) init = Initializer(In, Yield, Await)(given.init);
    return new Node('VariableDeclaration', given, 0, [bindingId, init], params);
  } else {
    let bindingPat = BindingPattern(Yield, Await)(given.id);
    let init = Initializer(In, Yield, Await)(given.init);
    return new Node('VariableDeclaration', given, 1, [bindingPat, init], params);
  }
}

module.exports = VariableDeclaration;
