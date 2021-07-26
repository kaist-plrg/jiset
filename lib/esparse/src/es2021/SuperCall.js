const Node = require('../Node');

// SuperCall[Yield, Await]: {
//   `super` Arguments[?Yield, ?Await]
// }

let SuperCall = (Yield, Await) => (given) => {
  const Arguments = require('./Arguments');
  let params = [Yield, Await];
  // TODO fix span
  const args = Arguments(Yield, Await)(given.arguments);
  return new Node('SuperCall', given, 0, [args], params);
}

module.exports = SuperCall;
