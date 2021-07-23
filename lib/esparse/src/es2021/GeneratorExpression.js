const Node = require('../Node');

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
  let ps = FormalParameters(true, false)(params);
  let b = GeneratorBody(body);
  return new Node('GeneratorExpression', given, 0, [binding, ps, b]);
}

module.exports = GeneratorExpression;
