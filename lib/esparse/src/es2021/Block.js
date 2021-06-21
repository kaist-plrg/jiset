const Node = require('../Node');

// Block[Yield, Await, Return] :
//    { StatementList[?Yield, ?Await, ?Return]_opt }
let Block = (Yield, Await, Return) => (given) => {
  const StatementList = require('./StatementList');
  let params = [Yield, Await, Return];
  let list = null;
  if (given.body.length > 0) list = StatementList(Yield, Await, Return)(given.body);
  return new Node('Block', given, 0, [list], params);
}

module.exports = Block;
