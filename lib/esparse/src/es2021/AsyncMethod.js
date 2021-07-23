const Node = require('../Node');

// AsyncMethod[Yield, Await]: {
//   `async` [no LineTerminator here] PropertyName[?Yield, ?Await] `(` UniqueFormalParameters[~Yield, +Await] `)` `{` AsyncFunctionBody `}`
// }

let AsyncMethod = (Yield, Await) => (given) => {
  
  const PropertyName = require('./PropertyName');
  const UniqueFormalParameters = require('./UniqueFormalParameters');
  const AsyncFunctionBody = require('./AsyncFunctionBody');
  
  let params = [Yield, Await];
  let name = PropertyName(Yield, Await)(given);
  let ps = UniqueFormalParameters(false, true)(given.value.params);

  let b = AsyncFunctionBody(given.value.body);
  return new Node('AsyncMethod', given, 0, [name, ps, b], params);
}

module.exports = AsyncMethod;
