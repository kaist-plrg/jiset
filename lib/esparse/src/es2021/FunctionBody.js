const Node = require('../Node');

// FunctionBody[Yield, Await] :
//    FunctionStatementList[?Yield, ?Await]
let FunctionBody = (Yield, Await) => (given) => {
  const FunctionStatementList = require('./FunctionStatementList');
  let params = [Yield, Await];
  let list = FunctionStatementList(Yield, Await)(given);
  return new Node('FunctionBody', given, 0, [list], params);
}

module.exports = FunctionBody;
