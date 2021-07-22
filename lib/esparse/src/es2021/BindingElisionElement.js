const Node = require('../Node');

// BindingElisionElement[Yield, Await]: {
//   Elision? BindingElement[?Yield, ?Await]
// }

// TODO check span info
let BindingElisionElement = (Yield, Await) => (given) => {
  let BindingElement = require('./BindingElement');
  let Elision = require('./Elision');
  let params = [Yield, Await];

  let binding = BindingElement(Yield, Await)(given.pop());
  let elision = null;
  if (given.length > 0) elision = Elision(given);
  
  return new Node('BindingElisionElement', given, 0, [elision, binding], params);
}

module.exports = BindingElisionElement;
