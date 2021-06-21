const Node = require('../Node');

// FunctionStatementList[Yield, Await] :
//    StatementList[?Yield, ?Await, +Return]_opt
let FunctionStatementList = (Yield, Await) => (given) => {
  const StatementList = require('./StatementList');
  let params = [Yield, Await];
  let list = null;
  if (given.body.length > 0) list = StatementList(Yield, Await, true)(given.body);
  return new Node('FunctionStatementList', given, 0, [list], params);
}

module.exports = FunctionStatementList;
