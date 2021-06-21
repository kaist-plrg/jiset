const Node = require('../Node');

// BindingIdentifier[Yield, Await] :
//    Identifier
//    yield
//    await
let BindingIdentifier = (Yield, Await) => (given) => {
  let Identifier = require('./Identifier');

  let params = [Yield, Await];
  switch (given.name) {
    case 'yield':
      return new Node('BindingIdentifier', given, 1, [], params);
    case 'await':
      return new Node('BindingIdentifier', given, 2, [], params);
    default:
      return new Node('BindingIdentifier', given, 0, [Identifier(given)], params);
  }
}

module.exports = BindingIdentifier;
