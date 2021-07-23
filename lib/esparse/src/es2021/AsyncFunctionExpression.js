const Node = require('../Node');

// AsyncFunctionExpression: {
//   `async` [no LineTerminator here] `function` BindingIdentifier[~Yield, +Await]? `(` FormalParameters[~Yield, +Await] `)` `{` AsyncFunctionBody `}`
// }

let AsyncFunctionExpression = (given) => {
  const BindingIdentifier = require('./BindingIdentifier');
  const FormalParameters = require('./FormalParameters');
  const AsyncFunctionBody = require('./AsyncFunctionBody');

  let { id, params, body } = given;

  let binding = null;
  if (id != null) binding = BindingIdentifier(false, true)(id);
  // TODO handle span
  let ps = FormalParameters(false, true)(params);
  let b = AsyncFunctionBody(body);
  return new Node('AsyncFunctionExpression', given, 0, [binding, ps, b]);
}

module.exports = AsyncFunctionExpression;
