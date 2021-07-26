const Node = require('../Node');
const { checkComma } = require('../Global');

// GeneratorExpression: {
//   `function` `*` BindingIdentifier[+Yield, ~Await]? `(` FormalParameters[+Yield, ~Await] `)` `{` GeneratorBody `}`
// }

let GeneratorExpression = (given) => {
  const BindingIdentifier = require('./BindingIdentifier');
  const FormalParameters = require('./FormalParameters');
  const GeneratorBody = require('./GeneratorBody');

  let { id, params, body } = given;

  let binding = null;
  if (id != null) binding = BindingIdentifier(true, false)(id);
  // TODO handle span
  let hasComma = checkComma(given.start, body.start, ')');
  let ps = FormalParameters(true, false)(params, hasComma);
  let b = GeneratorBody(body);
  return new Node('GeneratorExpression', given, 0, [binding, ps, b]);
}

module.exports = GeneratorExpression;
