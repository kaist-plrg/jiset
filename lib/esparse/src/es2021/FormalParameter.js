const Node = require('../Node');

// FormalParameter[Yield, Await] :
//    BindingElement[?Yield, ?Await]
let FormalParameter = (Yield, Await) => (given) => {
  const BindingElement = require('./BindingElement');
  let params = [Yield, Await];
  let elem = BindingElement(Yield, Await)(given);
  return new Node('FormalParameter', given, 0, [elem], params);
}

module.exports = FormalParameter;
