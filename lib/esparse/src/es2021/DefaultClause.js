const Node = require('../Node');

// DefaultClause[Yield, Await, Return] :
//    default : StatementList[?Yield, ?Await, ?Return]_opt
let DefaultClause = (Yield, Await, Return) => (given) => {
  const StatementList = require('./StatementList');
  let params = [Yield, Await, Return];
  let list = null;
  if (given.consequent.length > 0) {
    list = StatementList(Yield, Await, Return)(given.consequent);
  }
  return new Node('DefaultClause', given, 0, [list], params);
}

module.exports = DefaultClause;
