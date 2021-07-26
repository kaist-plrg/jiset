const Node = require('../Node');
const { checkComma } = require('../Global');

// AsyncGeneratorMethod[Yield, Await]: {
//   `async` [no LineTerminator here] `*` PropertyName[?Yield, ?Await] `(` UniqueFormalParameters[+Yield, +Await] `)` `{` AsyncGeneratorBody `}`
// }

let AsyncGeneratorMethod = (Yield, Await) => (given) => {
  const PropertyName = require('./PropertyName');
  const UniqueFormalParameters = require('./UniqueFormalParameters');
  const AsyncGeneratorBody = require('./AsyncGeneratorBody');
  
  let params = [Yield, Await];
  let name = PropertyName(Yield, Await)(given);
  
  let hasComma = checkComma(given.key.end, given.value.body.start, ')');
  let ps = UniqueFormalParameters(true, true)(given.value.params, hasComma);

  let b = AsyncGeneratorBody(given.value.body);
  return new Node('AsyncGeneratorMethod', given, 0, [name, ps, b], params);
}

module.exports = AsyncGeneratorMethod;
