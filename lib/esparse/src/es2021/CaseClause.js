const Node = require('../Node');

// CaseClause[Yield, Await, Return] :
//    case Expression[+In, ?Yield, ?Await] : StatementList[?Yield, ?Await, ?Return]_opt
let CaseClause = (Yield, Await, Return) => (given) => {
  const Expression = require('./Expression');
  const StatementList = require('./StatementList');
  let params = [Yield, Await, Return];
  let expr = Expression(true, Yield, Await)(given.test);
  let list = null;
  if (given.consequent.length > 0) {
    list = StatementList(Yield, Await, Return)(given.consequent);
  }
  return new Node('CaseClause', given, 0, [expr, list], params);
}

module.exports = CaseClause;
