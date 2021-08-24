const Node = require('../Node');
const { checkComma } = require('../Global');

// AsyncFunctionExpression: {
//   `async` [no LineTerminator here] `function` BindingIdentifier[~Yield, +Await]? `(` FormalParameters[~Yield, +Await] `)` `{` AsyncFunctionBody `}`
// }

let AsyncFunctionExpression = (given) => {
  const BindingIdentifier = require('./BindingIdentifier');
  const FormalParameters = require('./FormalParameters');
  const AsyncFunctionBody = require('./AsyncFunctionBody');

  let { id, params, body } = given;

  // set loc info for FormalParameters
  params.start = given.start;
  params.end = body.start;
  params.loc = given.loc;
  params.loc.end = body.loc.start;

  let binding = null;
  if (id != null) binding = BindingIdentifier(false, true)(id);
  // TODO handle span
  let hasComma = checkComma(given.start, body.start, ')');
  let ps = FormalParameters(false, true)(params, hasComma);
  let b = AsyncFunctionBody(body);
  return new Node('AsyncFunctionExpression', given, 0, [binding, ps, b]);
}

module.exports = AsyncFunctionExpression;
