const Node = require('../Node');
const { checkComma } = require('../Global');

// FunctionExpression :
//    function BindingIdentifier[~Yield, ~Await]_opt ( FormalParameters[~Yield, ~Await] ) { FunctionBody[~Yield, ~Await] }
let FunctionExpression = (given) => {
  const BindingIdentifier = require('./BindingIdentifier');
  const FormalParameters = require('./FormalParameters');
  const FunctionBody = require('./FunctionBody');
  let { id, params, body } = given;
  let x = null;
  if (id != null) x = BindingIdentifier(false, false)(id);
  let hasComma = checkComma(given.start, body.start, ')');
  let ps = FormalParameters(false, false)(params, hasComma);
  let b = FunctionBody(false, false)(body);
  return new Node('FunctionExpression', given, 0, [x, ps, b]);
}

module.exports = FunctionExpression;
