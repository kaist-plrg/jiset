const Node = require('../Node');

// BindingIdentifier[Yield, Await] :
//    Identifier
//    yield
//    await
let BindingIdentifier = (Yield, Await) => (given) => {
  const Identifier = require('./Identifier');
  let params = [Yield, Await];
  if (!Yield && !Await) 
    return new Node('BindingIdentifier', given, 0, [Identifier(given)], params);
  else switch (given.name) {
    case 'yield':
      return new Node('BindingIdentifier', given, 1, [], params);
    case 'await':
      return new Node('BindingIdentifier', given, 2, [], params);
    default:
      return new Node('BindingIdentifier', given, 0, [Identifier(given)], params);
  }
}

module.exports = BindingIdentifier;
