const Node = require('../Node');

// LabelIdentifier[Yield, Await]: {
//   Identifier |
//   [~Yield] `yield` |
//   [~Await] `await`
// }

let LabelIdentifier = (Yield, Await) => (given) => {
  const Identifier = require('./Identifier');
  let params = [Yield, Await];
  switch (given.name) {
    case 'yield':
      return new Node('LabelIdentifier', given, 1, [], params);
    case 'await':
      return new Node('LabelIdentifier', given, 2, [], params);
    default:
      return new Node('LabelIdentifier', given, 0, [Identifier(given)], params);
  }
}

module.exports = LabelIdentifier;
