const Node = require('../Node');
const { checkComma } = require('../Global');

// AsyncGeneratorExpression: {
//   `async` [no LineTerminator here] `function` `*` BindingIdentifier[+Yield, +Await]? `(` FormalParameters[+Yield, +Await] `)` `{` AsyncGeneratorBody `}`
// }

let AsyncGeneratorExpression = (given) => {
  const BindingIdentifier = require('./BindingIdentifier');
  const FormalParameters = require('./FormalParameters');
  const AsyncGeneratorBody = require('./AsyncGeneratorBody');

  let { id, params, body } = given;

  let binding = null;
  if (id != null) binding = BindingIdentifier(true, true)(id);
  // TODO handle span
  let hasComma = checkComma(given.start, body.start, ')');
  let ps = FormalParameters(true, true)(params, hasComma);
  let b = AsyncGeneratorBody(body);
  return new Node('AsyncGeneratorExpression', given, 0, [binding, ps, b]);
}

module.exports = AsyncGeneratorExpression;
