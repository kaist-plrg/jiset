const Node = require('../Node');

// ExpressionStatement[Yield, Await] :
//    [lookahead ∉ { {, function, async [no LineTerminator here] function, class, let [ }] Expression[+In, ?Yield, ?Await] ;
let ExpressionStatement = (Yield, Await) => (given) => {
  const Expression = require('./Expression');
  let params = [Yield, Await];
  let expr = Expression(true, Yield, Await)(given.expression);
  return new Node('ExpressionStatement', given, 0, [expr], params);
}

module.exports = ExpressionStatement;
