const Node = require('../Node');

// GeneratorBody :
//    FunctionBody[+Yield, ~Await]
let GeneratorBody = (given) => {
  const FunctionBody = require('./FunctionBody');
  let body = FunctionBody(true, false)(given);
  return new Node('GeneratorBody', given, 0, [body]);
}

module.exports = GeneratorBody;
