const Node = require('../Node');

// SwitchStatement[Yield, Await, Return] :
//    switch ( Expression[+In, ?Yield, ?Await] ) CaseBlock[?Yield, ?Await, ?Return]
let SwitchStatement = (Yield, Await, Return) => (given) => {
  const Expression = require('./Expression');
  const CaseBlock = require('./CaseBlock');
  let params = [Yield, Await, Return];
  let expr = Expression(true, Yield, Await)(given.discriminant);
  let block = CaseBlock(Yield, Await, Return)(given.cases);
  return new Node('SwitchStatement', given, 0, [expr, block], params);
}

module.exports = SwitchStatement;
