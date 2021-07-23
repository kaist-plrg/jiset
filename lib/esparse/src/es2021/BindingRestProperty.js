const Node = require('../Node');

// BindingRestProperty[Yield, Await]: {
//   `...` BindingIdentifier[?Yield, ?Await]
// }

let BindingRestProperty = (Yield, Await) => given => {
  let BindingIdentifier = require('./BindingIdentifier');
  let params = [Yield, Await];
  let id = BindingIdentifier(Yield, Await)(given.argument);
  return new Node('BindingRestProperty', given, 0, [id], params);
}

module.exports = BindingRestProperty;
