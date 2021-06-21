const Node = require('../Node');

// AsyncFunctionBody :
//    FunctionBody[~Yield, +Await]
let AsyncFunctionBody = (given) => {
  const FunctionBody = require('./FunctionBody');
  let body = FunctionBody(false, true)(given);
  return new Node('AsyncFunctionBody', given, 0, [body]);
}

module.exports = AsyncFunctionBody;
