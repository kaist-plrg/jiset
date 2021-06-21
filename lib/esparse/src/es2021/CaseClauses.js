const Node = require('../Node');

// CaseClauses[Yield, Await, Return] :
//    CaseClause[?Yield, ?Await, ?Return]
//    CaseClauses[?Yield, ?Await, ?Return] CaseClause[?Yield, ?Await, ?Return]
let CaseClauses = (Yield, Await, Return) => (given) => {
  const CaseClause = require('./CaseClause');
  let genChild = CaseClause(Yield, Await, Return);
  let params = [Yield, Await, Return];
  return Node.fromList('CaseClause', given, genChild, params);
}

module.exports = CaseClauses;
