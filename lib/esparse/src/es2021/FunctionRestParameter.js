const Node = require('../Node');

// FunctionRestParameter[Yield, Await] :
//    BindingRestElement[?Yield, ?Await]
let FunctionRestParameter = (Yield, Await) => (given) => {
  const BindingRestElement = require('./BindingRestElement');
  let params = [Yield, Await];
  let rest = BindingRestElement(Yield, Await)(given);
  return new Node('FunctionRestParameter', given, 0, [rest], params);
}

module.exports = FunctionRestParameter;
