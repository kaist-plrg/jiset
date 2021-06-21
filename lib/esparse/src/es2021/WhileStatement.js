const Node = require('../Node');

// WhileStatement[Yield, Await, Return] :
//    while ( Expression[+In, ?Yield, ?Await] ) Statement[?Yield, ?Await, ?Return]
let WhileStatement = (Yield, Await, Return) => (given) => {
  const Expression = require('./Expression');
  const Statement = require('./Statement');
  let params = [Yield, Await, Return];
  let expr = Expression(true, Yield, Await)(given.test);
  let stmt = Statement(Yield, Await, Return)(given.body);
  return new Node('WhileStatement', given, 0, [expr, stmt], params);
}

module.exports = WhileStatement;
