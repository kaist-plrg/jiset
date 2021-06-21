const Node = require('../Node');

// WithStatement[Yield, Await, Return] :
//    with ( Expression[+In, ?Yield, ?Await] ) Statement[?Yield, ?Await, ?Return]
let WithStatement = (Yield, Await, Return) => (given) => {
  const Expression = require('./Expression');
  const Statement = require('./Statement');
  let params = [Yield, Await, Return];
  let expr = Expression(true, Yield, Await)(given.object);
  let stmt = Statement(Yield, Await, Return)(given.body);
  return new Node('WithStatement', given, 0, [expr, stmt], params);
}

module.exports = WithStatement;
