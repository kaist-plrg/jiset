const Node = require('../Node');

// IdentifierReference[Yield, Await] :
//    Identifier
//    [~Yield]yield
//    [~Await]await
let IdentifierReference = (Yield, Await) => (given) => {
  let Identifier = require('./Identifier');

  let params = [Yield, Await];
  switch (given.name) {
    case 'yield':
      return new Node('IdentifierReference', given, 1, [], params);
    case 'await':
      return new Node('IdentifierReference', given, 2, [], params);
    default:
      return new Node('IdentifierReference', given, 0, [Identifier(given)], params);
  }
}

module.exports = IdentifierReference;
