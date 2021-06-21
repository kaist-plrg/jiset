const Node = require('../Node');

// CaseBlock[Yield, Await, Return] :
//    { CaseClauses[?Yield, ?Await, ?Return]_opt }
//    { CaseClauses[?Yield, ?Await, ?Return]_opt DefaultClause[?Yield, ?Await, ?Return] CaseClauses[?Yield, ?Await, ?Return]_opt }
let CaseBlock = (Yield, Await, Return) => (given) => {
  const CaseClauses = require('./CaseClauses');
  const DefaultClause = require('./DefaultClause');
  let params = [Yield, Await, Return];
  let defaultIdx = given.findIndex(x => x.test == null);
  let size = given.length;
  if (defaultIdx == -1) {
    let c = null;
    if (size > 0) c = CaseClauses(Yield, Await, Return)(given);
    return new Node('CaseBlock', {}, 0, [c], params);
  } else {
    let l = null;
    let ls = given.slice(0, defaultIdx);
    if (ls.length > 0) l = CaseClauses(Yield, Await, Return)(ls);
    let d = DefaultClause(Yield, Await, Return)(given[defaultIdx]);
    let r = null;
    let rs = given.slice(defaultIdx + 1);
    if (rs.length > 0) r = CaseClauses(Yield, Await, Return)(rs);
    return new Node('CaseBlock', {}, 1, [l, d, r], params);
  }
}

module.exports = CaseBlock;
