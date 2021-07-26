const Node = require('../Node');
const { checkComma } = require('../Global');

// AsyncMethod[Yield, Await]: {
//   `async` [no LineTerminator here] PropertyName[?Yield, ?Await] `(` UniqueFormalParameters[~Yield, +Await] `)` `{` AsyncFunctionBody `}`
// }

let AsyncMethod = (Yield, Await) => (given) => {
  
  const PropertyName = require('./PropertyName');
  const UniqueFormalParameters = require('./UniqueFormalParameters');
  const AsyncFunctionBody = require('./AsyncFunctionBody');
  
  let params = [Yield, Await];
  let name = PropertyName(Yield, Await)(given);
  let hasComma = checkComma(given.key.end, given.value.body.end, ')');
  let ps = UniqueFormalParameters(false, true)(given.value.params, hasComma);

  let b = AsyncFunctionBody(given.value.body);
  return new Node('AsyncMethod', given, 0, [name, ps, b], params);
}

module.exports = AsyncMethod;
