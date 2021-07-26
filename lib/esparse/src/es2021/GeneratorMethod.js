const Node = require('../Node');
const { checkComma } = require('../Global');

// GeneratorMethod[Yield, Await]: {
//   `*` PropertyName[?Yield, ?Await] `(` UniqueFormalParameters[+Yield, ~Await] `)` `{` GeneratorBody `}`
// }

let GeneratorMethod = (Yield, Await) => (given) => {
  const PropertyName = require('./PropertyName');
  const UniqueFormalParameters = require('./UniqueFormalParameters');
  const GeneratorBody = require('./GeneratorBody');
  
  let params = [Yield, Await];
  let name = PropertyName(Yield, Await)(given);
  let hasComma = checkComma(given.key.end, given.value.body.end, ')');
  let ps = UniqueFormalParameters(true, false)(given.value.params, hasComma);

  let b = GeneratorBody(given.value.body);
  return new Node('GeneratorMethod', given, 0, [name, ps, b], params);
}

module.exports = GeneratorMethod;
