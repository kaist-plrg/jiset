const Node = require('../Node');

// AsyncGeneratorMethod[Yield, Await]: {
//   `async` [no LineTerminator here] `*` PropertyName[?Yield, ?Await] `(` UniqueFormalParameters[+Yield, +Await] `)` `{` AsyncGeneratorBody `}`
// }

let AsyncGeneratorMethod = (Yield, Await) => (given) => {
  const PropertyName = require('./PropertyName');
  const UniqueFormalParameters = require('./UniqueFormalParameters');
  const AsyncGeneratorBody = require('./AsyncGeneratorBody');
  
  let params = [Yield, Await];
  let name = PropertyName(Yield, Await)(given);
  let ps = UniqueFormalParameters(true, true)(given.value.params);

  let b = AsyncGeneratorBody(given.value.body);
  return new Node('AsyncGeneratorMethod', given, 0, [name, ps, b], params);
}

module.exports = AsyncGeneratorMethod;
