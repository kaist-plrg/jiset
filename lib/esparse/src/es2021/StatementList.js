const Node = require('../Node');

// StatementList[Yield, Await, Return] :
//    StatementListItem[?Yield, ?Await, ?Return]
//    StatementList[?Yield, ?Await, ?Return] StatementListItem[?Yield, ?Await, ?Return]
let StatementList = (Yield, Await, Return) => (given) => {
  const StatementListItem = require('./StatementListItem');
  let genChild = StatementListItem(Yield, Await, Return);
  let params = [Yield, Await, Return];
  return Node.fromList('StatementList', given, genChild, params);
}

module.exports = StatementList;
