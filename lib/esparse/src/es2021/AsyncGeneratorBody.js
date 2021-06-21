const Node = require('../Node');

// AsyncGeneratorBody :
//    FunctionBody[+Yield, +Await]
let AsyncGeneratorBody = (given) => {
  const FunctionBody = require('./FunctionBody');
  let body = FunctionBody(true, true)(given);
  return new Node('AsyncGeneratorBody', given, 0, [body]);
}

module.exports = AsyncGeneratorBody;
