const Node = require('../Node');

// AsyncArrowBindingIdentifier[Yield]: {
//   BindingIdentifier[?Yield, +Await]
// }

let AsyncArrowBindingIdentifier = (Yield) => (given) => {
  const BindingIdentifier = require('./BindingIdentifier');
  let params = [Yield];
  let id = BindingIdentifier(Yield, true)(given);
  return new Node('AsyncArrowBindingIdentifier', given, 0, [id], params);

}

module.exports = AsyncArrowBindingIdentifier;
