const Node = require('../Node');

// AwaitExpression[Yield] :
//    await UnaryExpression[?Yield, +Await]
let AwaitExpression = (Yield) => (given) => {
  const UnaryExpression = require('./UnaryExpression');
  let params = [Yield];
  let expr = UnaryExpression(Yield, true)(given.argument);
  return new Node('AwaitExpression', given, 0, [expr], params);
}

module.exports = AwaitExpression;
